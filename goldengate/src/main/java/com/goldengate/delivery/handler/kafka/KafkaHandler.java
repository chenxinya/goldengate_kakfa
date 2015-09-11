/*
 *
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 *
 */
package com.goldengate.delivery.handler.kafka;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.goldengate.atg.datasource.AbstractHandler;
import com.goldengate.atg.datasource.DsConfiguration;
import com.goldengate.atg.datasource.DsEvent;
import com.goldengate.atg.datasource.DsOperation;
import com.goldengate.atg.datasource.DsTransaction;
import com.goldengate.atg.datasource.GGDataSource.Status;
import com.goldengate.atg.datasource.adapt.Op;
import com.goldengate.atg.datasource.meta.DsMetaData;
import com.goldengate.delivery.handler.kafka.operations.OperationHandler;
import com.rogers.kafka.serializers.EncryptedMessage;
import com.rogers.kafka.serializers.Encryptor;
import com.goldengate.delivery.handler.kafka.util.OperationTypes;
import com.goldengate.delivery.handler.kafka.KafkaProducerWrapper;
import com.goldengate.delivery.handler.kafka.ProducerRecordWrapper;

//TODO: Fix the desc
/**
 * KafkaHandler is an extension of GoldenGate Java Adapters - "EventHandlers".
 * It operates on the column values received from the operations, creates Kafka
 * messages out of the column values and publishes the events to Kafka when the
 * transaction commit occurs.
 * 
 * In "tx" mode, the events will be buffered till the transaction commit is
 * received and all the events will be published in batch, once the transaction
 * is committed. In "op" mode, the events will be published on every
 * operation/record basis.
 * 
 * The Kafka Java Client (as of Kafka 0.8.2) is asynchronous and support message
 * batching/buffering. As such, the "tx" and "op" modes should have comparable
 * performance.
 * 
 * 
 * Considering a table "TCUST" with columns "CUST_ID", "CUST_NAME", "ADDRESS"
 * with a record being inserted into it say "10001","Kafka Admin","Los Angles".
 * The final data published into Flume would be similar to the following.
 * (Assuming "," as the configured delimiter) ### OperationType,Col-1, Col-2,
 * Col-3, Operation Timestamp ### I,10001,Kafka Admin,Los Angles,2014-12-18
 * 08:28:02.000000
 * 
 * The Operation Type and Operation Timestamp are configurable. By default both
 * Operation Type and Operation Timestamp will be part of the delimited
 * separated values.
 * 
 * @author Eugene Miretsky
 * 
 * */

public class KafkaHandler extends AbstractHandler {
	final private static Logger logger = LoggerFactory
			.getLogger(KafkaHandler.class);

	// TODO: Move it to a test class

	/** Producer for Kakfa */
	private KafkaProducerWrapper producer;

	/**
	 * Config file for Kafka Producer
	 * http://kafka.apache.org/082/javadoc/org/apache
	 * /kafka/clients/producer/KafkaProducer.html
	 */
	private String kafkaConfigFile;
	private final String KAFKA_CONFIG_FILE = "kafka.properties";

	/**
	 * Indicates if the operation timestamp should be included as part of output
	 * in the delimited separated values true - Operation timestamp will be
	 * included in the output false - Operation timestamp will not be included
	 * in the output
	 **/
	private Boolean includeOpTimestamp = true;

	/**
	 * Collection to store kafka messages until the "transaction commit" event
	 * is received in "tx" mode, upon which the list will be cleared
	 */
	private List<ProducerRecordWrapper> events = new ArrayList<ProducerRecordWrapper>();
	private HandlerProperties handlerProperties;

	@Override
	public void init(DsConfiguration arg0, DsMetaData arg1) {
		kafkaConfigFile = KAFKA_CONFIG_FILE; // set default value
		super.init(arg0, arg1);
		try {
			producer = new KafkaProducerWrapper(kafkaConfigFile);
		} catch (IOException e) {
			logger.error("Exception: " + e);
		}
		// Setting initial properties in HandlerProperties object, which will be
		// used for communicating the
		// information from Flume AbstractHandler to Operation Handlers
		//
		initializeHandlerProperties();
		logger.info("Done Initializing Kafka Handler");
	}

	@Override
	public Status operationAdded(DsEvent e, DsTransaction tx,
			DsOperation dsOperation) {

		Status status = Status.OK;
		super.operationAdded(e, tx, dsOperation);
		Op op = new Op(dsOperation, getMetaData().getTableMetaData(
				dsOperation.getTableName()), getConfig());
		logger.debug("Operation added event. Operation type = "
				+ dsOperation.getOperationType());
		/**
		 * Get the class defined for the incoming operation type from
		 * OperationTypes enum and instantiate the operation handler class. Each
		 * Operation Handler class creates the flume event.
		 * */
		OperationTypes operationTypes = OperationTypes.valueOf(dsOperation
				.getOperationType().toString());
		OperationHandler operationHandler = operationTypes
				.getOperationHandler();
		if (operationHandler != null) {
			try {
				operationHandler.process(op, handlerProperties);
				/** Increment the total number of operations */
				handlerProperties.totalOperations++;
			} catch (Exception e1) {
				status = Status.ABEND;
				logger.error("Unable to process operation.", e1);
			}
		} else {
			status = Status.ABEND;
			logger.error("Unable to instantiate operation handler.");
		}

		return status;
	}

	@Override
	public Status transactionCommit(DsEvent e, DsTransaction tx) {
		logger.debug("Transaction commit event ");
		super.transactionCommit(e, tx);
		Status status = Status.OK;
		/**
		 * Publish the events to flume using the rpc client once the transaction
		 * commit event is received.
		 * 
		 * In "op" mode, Event will be published to kafka on every
		 * "operationAdded" event, as transaction commit would be invoked for
		 * every operation.
		 * 
		 * In "tx" mode, Events will be buffered on every "opeartionAdded" event
		 * and published to flume once the transaction commit event is received.
		 * 
		 * */
		status = publishEvents();
		handlerProperties.totalTxns++;

		return status;
	}

	@Override
	public Status metaDataChanged(DsEvent e, DsMetaData meta) {
		logger.info("Metadata change event");
		return super.metaDataChanged(e, meta);
	}

	@Override
	public Status transactionBegin(DsEvent e, DsTransaction tx) {
		logger.info("Transaction begin event");
		return super.transactionBegin(e, tx);
	}

	@Override
	public String reportStatus() {
		logger.info("Reporting Status ");
		StringBuilder sb = new StringBuilder();
		sb.append("Status report: mode=").append(getMode());
		sb.append(", transactions=").append(handlerProperties.totalTxns);
		sb.append(", operations=").append(handlerProperties.totalOperations);
		sb.append(", inserts=").append(handlerProperties.totalInserts);
		sb.append(", updates=").append(handlerProperties.totalUpdates);
		sb.append(", deletes=").append(handlerProperties.totalDeletes);

		logger.info("Final Status " + sb.toString());
		return sb.toString();
	}

	@Override
	public void destroy() {
		logger.info("Destroy event");
		/**
		 * Close the connection to flume.
		 * */
		// this.rpcClient.close();
		super.destroy();
	}

	private Status publishEvents() {
		logger.info("Publishing events to Kafka. Events size = " + this.events);
		Status status = Status.OK;
		/**
		 * Publishing the events to Kafka.
		 * */
		if (!this.events.isEmpty()) {
			try {
				for (ProducerRecordWrapper rec : this.handlerProperties.events) {
					this.producer.send(rec);
				}
			} catch (Exception e1) {
				status = Status.ABEND;
				logger.error("Unable to deliver events. Events size : "
						+ events.size(), e1);
			}

			this.handlerProperties.events.clear();
		} else {
			logger.warn("No events available to publish.");
		}
		return status;
	}

	private void initializeHandlerProperties() {
		this.handlerProperties = new HandlerProperties();
		this.handlerProperties.events = this.events;
		this.handlerProperties.includeOpTimestamp = this.includeOpTimestamp;
	}

	public String getKafkaConfigFile() {
		return kafkaConfigFile;
	}

	public void setKafkaConfigFile(String delimiter) {
		this.kafkaConfigFile = delimiter;
	}

	public List<ProducerRecordWrapper> getEvents() {
		return events;
	}

	public void setEvents(List<ProducerRecordWrapper> events) {
		this.events = events;
	}

	public HandlerProperties getHandlerProperties() {
		return handlerProperties;
	}

	public void setHandlerProperties(HandlerProperties handlerProperties) {
		this.handlerProperties = handlerProperties;
	}

	public Boolean getIncludeOpTimestamp() {
		return includeOpTimestamp;
	}

	public void setIncludeOpTimestamp(Boolean includeOpTimestamp) {
		this.includeOpTimestamp = includeOpTimestamp;
	}

}
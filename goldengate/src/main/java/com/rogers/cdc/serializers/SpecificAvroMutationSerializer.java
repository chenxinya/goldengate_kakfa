package com.rogers.cdc.serializers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rogers.cdc.api.mutations.Column;
import com.rogers.cdc.api.mutations.InsertMutation;
import com.rogers.cdc.api.mutations.Mutation;
import com.rogers.cdc.api.mutations.RowMutation;
import com.rogers.cdc.api.mutations.UpdateMutation;
import com.rogers.cdc.exceptions.InvalidTypeException;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.serializers.AbstractKafkaAvroDeserializer;
//import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
//import io.confluent.kafka.serializers.NonRecordContainer;

public class SpecificAvroMutationSerializer extends AbstractAvroMutationSerialzer implements
		MutationSerializer {
	final private static Logger logger = LoggerFactory
			.getLogger(SpecificAvroMutationSerializer.class);
	
	  public static final String SCHEMAS_CACHE_SIZE_CONFIG = "schemas.cache.config";
	  private static final int SCHEMAS_CACHE_SIZE_DEFAULT = 1000;
	
	  private SchemaRegistryClient schemaRegistry;
	  private Serializer serializer;
	
	
	 protected  GenericData.Record avroRecord(Mutation op, Schema schema){
		    GenericData.Record record = new GenericData.Record(schema);
			addHeader(record, op);
			addBody(record,op);
			return record; 
	  }
      protected void addHeader(GenericRecord record, Mutation op) {
			String tableName = op.getTableName();
		    String schemaName = op.getSchemaName();
		    byte opType = op.getMagicByte();
		    record.put("table", tableName);
		    record.put("schema", schemaName);
		    record.put("op_type", opType);
}
	 private  void addBody(GenericRecord record, Mutation op){
	        switch(op.getType()){
	           case INSERT:
	           case UPDATE:
	           {
	        	   RowMutation mutation =  op.getMutation();
	        	   this.processRowOp(mutation,record);
	        	   break;
	           }
	           case  DELETE: {   	   
	        	   break;
	           }         
	           case PKUPDATE: 	
	        	   logger.error("The operation type PKUPDATE on table=[" + op.getTableName() + "]" + "is not supported");
	        	   throw new IllegalArgumentException("KafkaAvroHandler::addBody PKUPDATE operation not supported");   
	           default:
	        	   logger.error("The operation type " + op.getType() + " on  operation: table=[" + op.getTableName() + "]" + "is not supported");
	        	   throw new IllegalArgumentException("KafkaAvroHandler::addBody Unknown operation type");                                                                            
       }                                                                                              
	    }

		protected void processRowOp(RowMutation op, GenericRecord record) {
			     for(Map.Entry<String,Column> column : op.getRow().getColumns().entrySet()) {  
			    	   String name = column.getKey(); 
			    	   Object val = column.getValue().getValue();
			    	     try{		
			    	    	 record.put(name, val);
			    	     } catch (ClassCastException e) {
			    	          throw new InvalidTypeException("Invalid column type: " + e.getMessage());
			    	     }
			     } 
		}

	@Override
	public void configure(Map<String, ?> configs) {
		serializer = new KafkaAvroSerializer();
		serializer.configure(configs, false); // This usually gets called by Kafka...but we have to call it here since we never pass the serialzer to Kafk	
	}


	@Override
	//TODO
	  protected  byte[] serializeAvro( GenericData.Record record,  Schema schema, String topic,  byte opType) throws IOException {
			    /*EncoderFactory encoderFactory = EncoderFactory.get();
			    DatumWriter<GenericRecord> writer  = new GenericDatumWriter<GenericRecord>();
			    writer.setSchema(schema);
			    ByteArrayOutputStream out = new ByteArrayOutputStream();
			    out.write(PROTO_MAGIC_V0);
			    out.write(ByteBuffer.allocate(opTypeSize).put(opType).array() );
			    out.write(ByteBuffer.allocate(idSize).putShort(schemaId).array());
			    BinaryEncoder enc = encoderFactory.binaryEncoder(out, null);
			    writer.write(record, enc);
			    enc.flush();
			    return out.toByteArray();
			    */	
		      return serializer.serialize(topic, record);

			    
			    
	  }

	@Override
	public void close() {
		

	}

}
package com.rogers.goldengate.handlers;

import java.io.IOException;

import com.rogers.goldengate.api.mutations.Mutation;

//TODO this should be an interface
abstract public class Handler {
	public abstract void processOp(Mutation op);
	public  abstract void flush();
	void handleAlter(Mutation op){
		assert false : "Producer::handleAlter() not implimented";
	}

}
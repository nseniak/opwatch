package com.untrackr.alerter.processor.common;

import javax.script.ScriptException;

public class ExceptionContext {

	/**
	 * If the exception was thrown by a processor, location of this processor; null otherwise
	 */
	private ProcessorLocation processorLocation;
	/**
	 * If the exception was thrown by the execution of a callback, location of the script where the exception occurred;
	 * null otherwise.
	 */
	private CallbackErrorLocation callbackErrorLocation;
	/**
	 * If the exception was thrown when processing a payload, this payload; null otherwise.
	 */
	private Payload payload;

	private ExceptionContext() {
	}

	/**
	 * Toplevel
	 */
	public static ExceptionContext makeToplevel() {
		return new ExceptionContext();
	}

	/**
	 * Script executed globally
	 */
	public static ExceptionContext makeToplevelScript(ScriptException e) {
		ExceptionContext context = new ExceptionContext();
		context.callbackErrorLocation = new CallbackErrorLocation(ValueLocation.makeToplevel(), e);
		return context;
	}

	/**
	 * Script invoked from a processor
	 */
	public static ExceptionContext makeProcessorPayloadScriptCallback(Processor processor, CallbackErrorLocation callbackErrorLocation, Payload payload) {
		ExceptionContext context = new ExceptionContext();
		context.processorLocation = processor.getLocation();
		context.callbackErrorLocation = callbackErrorLocation;
		context.payload = payload;
		return context;
	}

	public static ExceptionContext makeProcessorNoPayloadScriptCallback(Processor processor, CallbackErrorLocation callbackErrorLocation) {
		return makeProcessorPayloadScriptCallback(processor, callbackErrorLocation, null);
	}

	/**
	 * Processor error, outside of a script
	 */
	public static ExceptionContext makeProcessorPayload(Processor processor, Payload payload) {
		ExceptionContext context = new ExceptionContext();
		context.processorLocation = processor.getLocation();
		context.payload = payload;
		return context;
	}

	public static ExceptionContext makeProcessorNoPayload(Processor processor) {
		return makeProcessorPayload(processor, null);
	}

	/**
	 * Error occurring during processor construction
	 */
	public static ExceptionContext makeProcessorFactory(String processorName) {
		ExceptionContext context = new ExceptionContext();
		context.processorLocation = new ProcessorLocation(processorName);
		return context;
	}

	public ProcessorLocation getProcessorLocation() {
		return processorLocation;
	}

	public CallbackErrorLocation getCallbackErrorLocation() {
		return callbackErrorLocation;
	}

	public Payload getPayload() {
		return payload;
	}

}

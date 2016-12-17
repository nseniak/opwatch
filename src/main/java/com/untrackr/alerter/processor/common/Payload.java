package com.untrackr.alerter.processor.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.untrackr.alerter.service.ProcessorService;

/**
 * Represents the output of a processor.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "_type")
public class Payload {

	/**
	 * Time at which the payload was generated.
	 */
	private long timestamp;
	/**
	 * Hostname on which the payload was generated.
	 */
	private String hostname;
	/**
	 * Name of the processor that generated this payload.
	 */
	private ProcessorLocation producer;
	/**
	 * The previous payload, or null if this is a root payload.
	 */
	private Payload previous;
	/**
	 * The payload value.
	 */
	private Object value;

	protected Payload(long timestamp, String hostname, ProcessorLocation producer, Payload previous, Object value) {
		this.timestamp = timestamp;
		this.hostname = hostname;
		this.producer = producer;
		this.previous = previous;
		this.value = value;
	}

	public static Payload makeRoot(ProcessorService processorService, Processor producer, Object value) {
		return new Payload(System.currentTimeMillis(), processorService.getHostName(), producer.getLocation(), null, value);
	}

	public static Payload makeTransformed(ProcessorService processorService, Processor producer, Payload previous, Object value) {
		return new Payload(System.currentTimeMillis(), processorService.getHostName(), producer.getLocation(), previous, value);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getHostname() {
		return hostname;
	}

	public ProcessorLocation getProducer() {
		return producer;
	}

	public Payload getPrevious() {
		return previous;
	}

	public Object getValue() {
		return value;
	}

}

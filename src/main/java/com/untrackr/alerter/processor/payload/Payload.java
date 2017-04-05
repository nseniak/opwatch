package com.untrackr.alerter.processor.payload;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ProcessorLocation;
import com.untrackr.alerter.service.ProcessorService;

/**
 * Represents the output of a processor.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "_type")
public class Payload<V> {

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
	private Payload<?> previous;
	/**
	 * The payload value.
	 */
	private V value;

	protected Payload(long timestamp, String hostname, ProcessorLocation producer, Payload<?> previous, V value) {
		this.timestamp = timestamp;
		this.hostname = hostname;
		this.producer = producer;
		this.previous = previous;
		this.value = value;
	}

	public static <V> Payload makeRoot(ProcessorService processorService, Processor producer, V value) {
		return new Payload<>(System.currentTimeMillis(), processorService.config().hostName(), producer.getLocation(), null, value);
	}

	public static <V> Payload makeTransformed(ProcessorService processorService, Processor producer, Payload previous, V value) {
		return new Payload<>(System.currentTimeMillis(), processorService.config().hostName(), producer.getLocation(), previous, value);
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

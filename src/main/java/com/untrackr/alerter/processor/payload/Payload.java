package com.untrackr.alerter.processor.payload;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.service.ProcessorService;

/**
 * Represents the output of a processor.
 */
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
	 * Http port of the alerter on which the payload was generated.
	 */
	private int port;
	/**
	 * Name of the processor that generated this payload.
	 */
	private String producer;
	/**
	 * The previous payload, or null if this is a root payload.
	 */
	private Payload<?> previous;
	/**
	 * The payload value.
	 */
	private V value;
	/**
	 * Optional metadata
	 */
	private Object metadata;

	private Payload() {
	}

	private Payload(long timestamp, String hostname, int port, String producer, Payload<?> previous, V value) {
		this.timestamp = timestamp;
		this.hostname = hostname;
		this.port = port;
		this.producer = producer;
		this.previous = previous;
		this.value = value;
	}

	public static <V> Payload<V> makeRoot(ProcessorService processorService, Processor producer, V value) {
		return new Payload<>(System.currentTimeMillis(), processorService.hostName(), processorService.port(), producer.getName(), null, value);
	}

	public static <V> Payload<V> makeTransformed(ProcessorService processorService, Processor producer, Payload<?> previous, V value) {
		return new Payload<>(System.currentTimeMillis(), processorService.hostName(), processorService.port(), producer.getName(), previous, value);
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public Payload<?> getPrevious() {
		return previous;
	}

	public void setPrevious(Payload<?> previous) {
		this.previous = previous;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public Object getMetadata() {
		return metadata;
	}

	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}

}

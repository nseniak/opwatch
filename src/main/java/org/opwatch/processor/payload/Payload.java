package org.opwatch.processor.payload;

import org.opwatch.processor.common.Processor;
import org.opwatch.service.ProcessorService;
import sun.jvm.hotspot.utilities.soql.ScriptObject;

/**
 * Represents the output of a processor.
 */
public class Payload extends PayloadPojoValue {

	/**
	 * Time at which the payload was generated.
	 */
	private long timestamp;
	/**
	 * Hostname on which the payload was generated.
	 */
	private String hostname;
	/**
	 * Http port of the service which generated the payload.
	 */
	private Integer port;
	/**
	 * Id of the producer that generated this payload.
	 */
	private String producerId;
	/**
	 * Name of the processor that generated this payload.
	 */
	private String producer;
	/**
	 * The previous payload, or null if this is a root payload.
	 */
	private Payload previous;
	/**
	 * The payload value.
	 */
	private Object value;
	/**
	 * Optional metadata
	 */
	private Object metadata = ScriptObject.UNDEFINED;

	private Payload() {
	}

	private Payload(long timestamp, String hostname, Integer port, String producerId, String producer, Payload previous, Object value) {
		this.timestamp = timestamp;
		this.hostname = hostname;
		this.port = port;
		this.producerId = producerId;
		this.producer = producer;
		this.previous = previous;
		this.value = value;
	}

	public static Payload makeRoot(ProcessorService processorService, Processor producer, Object value) {
		return new Payload(System.currentTimeMillis(), processorService.hostName(), processorService.port(), producer.getId(), producer.getName(), null, value);
	}

	public static Payload makeTransformed(ProcessorService processorService, Processor producer, Payload previous, Object value) {
		return new Payload(System.currentTimeMillis(), processorService.hostName(), processorService.port(), producer.getId(), producer.getName(), previous, value);
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

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getProducerId() {
		return producerId;
	}

	public void setProducerId(String producerId) {
		this.producerId = producerId;
	}

	public String getProducer() {
		return producer;
	}

	public void setProducer(String producer) {
		this.producer = producer;
	}

	public Payload getPrevious() {
		return previous;
	}

	public void setPrevious(Payload previous) {
		this.previous = previous;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getMetadata() {
		return metadata;
	}

	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}

}

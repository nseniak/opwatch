package com.untrackr.alerter.processor.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.untrackr.alerter.model.common.JsonMap;
import com.untrackr.alerter.service.ProcessorService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Represents the output of a processor.
 */
public class Payload {

	private ProcessorService processorService;
	private Processor producer;
	private Object jsonObject;
	private Payload input;
	private long timestamp;
	private String hostname;

	private Payload(ProcessorService processorService, Processor producer, Object jsonObject, Payload input, long timestamp, String hostname) {
		this.processorService = processorService;
		this.producer = producer;
		this.jsonObject = jsonObject;
		this.input = input;
		this.timestamp = timestamp;
		this.hostname = hostname;
	}

	public static Payload makeRoot(ProcessorService processorService, Processor producer, Object object) {
		return new Payload(
				processorService,
				producer,
				object,
				null,
				System.currentTimeMillis(),
				processorService.getHostName()
		);
	}

	public static Payload makeFiltered(ProcessorService processorService, Processor producer, Object object, Payload input) {
		return new Payload(
				processorService,
				producer,
				object,
				input,
				input.getTimestamp(),
				input.getHostname()
		);
	}

	private static JsonMap makeJsonMap(ProcessorService processorService, Object object) {
		return processorService.getObjectMapper().convertValue(object, JsonMap.class);
	}

	public String asText() {
		try {
			return processorService.getObjectMapper().writeValueAsString(jsonObject);
		} catch (JsonProcessingException e) {
			return "<cannot convert to string>";
		}
	}

	public List<Payload> inputList() {
		LinkedList<Payload> list = new LinkedList<>();
		Payload cursor = this;
		while (cursor != null) {
			list.push(cursor);
			cursor = cursor.getInput();
		}
		return list;
	}

	public String pathDescriptor(Processor last) {
		StringBuilder builder = new StringBuilder();
		String lastFilename = null;
		String delimiter = "";
		List<Processor> producers = new ArrayList<>(inputList().stream().map(Payload::getProducer).collect(toList()));
		producers.add(last);
		for (Processor producer : producers) {
			if (!getProducer().getPath().isEmpty()) {
				String filename = getProducer().getPath().last().getFilename();
				if (!filename.equals(lastFilename)) {
					lastFilename = filename;
					builder.append("[").append(filename).append("] > ");
				}
			}
			builder.append(delimiter).append(producer.descriptor());
			delimiter = " > ";
		}
		return builder.toString();
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public void setProcessorService(ProcessorService processorService) {
		this.processorService = processorService;
	}

	public Processor getProducer() {
		return producer;
	}

	public Object getJsonObject() {
		return jsonObject;
	}

	public Payload getInput() {
		return input;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getHostname() {
		return hostname;
	}

}

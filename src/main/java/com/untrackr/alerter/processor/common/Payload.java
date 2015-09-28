package com.untrackr.alerter.processor.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.untrackr.alerter.model.common.JsonObject;
import com.untrackr.alerter.service.ProcessorService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.untrackr.alerter.model.common.JsonObject.deepCopy;
import static java.util.stream.Collectors.toList;

/**
 * Represents the output of a processor.
 */
public class Payload {

	private ProcessorService processorService;
	private Processor producer;
	private JsonObject jsonObject;
	private Payload input;

	public Payload(ProcessorService processorService, Processor producer, Object object, Payload input) {
		this.processorService = processorService;
		this.producer = producer;
		if (object instanceof JsonObject) {
			this.jsonObject = deepCopy((JsonObject) object);
		} else {
			this.jsonObject = processorService.getObjectMapper().convertValue(object, JsonObject.class);
		}
		this.input = input;
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
		String lastPathName = null;
		String delimiter = "";
		List<Processor> producers = new ArrayList<>(inputList().stream().map(Payload::getProducer).collect(toList()));
		producers.add(last);
		for (Processor producer : producers) {
			if (!getProducer().getPath().isEmpty()) {
				String pathName = getProducer().getPath().last();
				if (!pathName.equals(lastPathName)) {
					lastPathName = pathName;
					builder.append("[").append(pathName).append("]");
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

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	public Payload getInput() {
		return input;
	}

}

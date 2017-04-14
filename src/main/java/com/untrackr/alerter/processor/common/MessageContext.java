package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;

public class MessageContext {

	enum EmitterType {
		global, processor, factory
	}

	private EmitterType emitterType;
	private String hostname;
	private String alerterId;
	private String processorName;
	private Payload<?> payload;
	private ScriptStack stack;

	private MessageContext() {
	}

	private MessageContext(EmitterType emitterType,
												 String hostname,
												 String alerterId,
												 String processorName,
												 Payload<?> payload,
												 ScriptStack stack) {
		this.emitterType = emitterType;
		this.hostname = hostname;
		this.alerterId = alerterId;
		this.processorName = processorName;
		this.payload = payload;
		this.stack = stack;
	}

	public static MessageContext makeGlobal(ProcessorService processorService, ScriptStack stack) {
		String hostname = processorService.hostName();
		String alerterId = processorService.getId();
		return new MessageContext(EmitterType.global, hostname, alerterId, null, null, stack);
	}

	public static MessageContext makeProcessor(ProcessorService processorService, String processorName, Payload<?> payload, ScriptStack stack) {
		String hostname = processorService.hostName();
		String alerterId = processorService.getId();
		return new MessageContext(EmitterType.global, hostname, alerterId, processorName, payload, stack);
	}

	public static MessageContext makeFactory(ProcessorService processorService, String processorName, ScriptStack stack) {
		String hostname = processorService.hostName();
		String alerterId = processorService.getId();
		return new MessageContext(EmitterType.global, hostname, alerterId, processorName, null, stack);
	}

	public String descriptor() {
		switch (emitterType) {
			case global:
				return hostname;
			case processor:
			case factory:
				return processorName + " on " + hostname;
		}
		throw new IllegalStateException("unknown kind: " + emitterType.name());
	}

	public EmitterType getEmitterType() {
		return emitterType;
	}

	public void setEmitterType(EmitterType emitterType) {
		this.emitterType = emitterType;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getAlerterId() {
		return alerterId;
	}

	public void setAlerterId(String alerterId) {
		this.alerterId = alerterId;
	}

	public String getProcessorName() {
		return processorName;
	}

	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	public Payload<?> getPayload() {
		return payload;
	}

	public void setPayload(Payload<?> payload) {
		this.payload = payload;
	}

	public ScriptStack getStack() {
		return stack;
	}

	public void setStack(ScriptStack stack) {
		this.stack = stack;
	}

}

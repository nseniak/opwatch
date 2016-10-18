package com.untrackr.alerter.processor.producer.receive;

import com.untrackr.alerter.common.RemotePayload;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ProcessorExecutionException;
import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.HttpService;
import com.untrackr.alerter.service.ProcessorService;

public class Receive extends Producer implements HttpService.PostBodyConsumer {

	private String urlPath;

	public Receive(ProcessorService processorService, ScriptStack stack, String urlPath) {
		super(processorService, stack);
		this.urlPath = urlPath;
	}

	@Override
	public void doStart() {
		processorService.getHttpService().addPostBodyConsumer(urlPath, this);
	}

	@Override
	protected void doStop() {
		processorService.getHttpService().removePostBodyConsumer(urlPath, this);
	}

	@Override
	public void consume(Object input) {
		try {
			RemotePayload remotePayload = processorService.getObjectMapper().convertValue(input, RemotePayload.class);
			outputReceived(remotePayload);
		} catch (IllegalArgumentException e) {
			throw new ProcessorExecutionException("invalid input: " + processorService.valueAsString(input), this);
		}
	}

	@Override
	public String identifier() {
		return urlPath;
	}
}

package com.untrackr.alerter.processor.producer.receive;

import com.untrackr.alerter.common.RemotePayload;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.HttpService;
import com.untrackr.alerter.service.ProcessorService;

public class Receive extends Producer implements HttpService.PostBodyConsumer {

	private String urlPath;

	public Receive(ProcessorService processorService, IncludePath path, String urlPath) {
		super(processorService, path);
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
			throw new RuntimeProcessorError("invalid input: " + processorService.valueAsString(input), this, null);
		}
	}

	@Override
	public String identifier() {
		return urlPath;
	}
}
package com.untrackr.alerter.processor.primitives.producer.receive;

import com.untrackr.alerter.processor.common.ProcessorVoidExecutionContext;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.producer.Producer;
import com.untrackr.alerter.service.HttpService;
import com.untrackr.alerter.service.ProcessorService;

public class Receive extends Producer<ReceiveConfig> implements HttpService.PostBodyHandle {

	private String urlPath;

	public Receive(ProcessorService processorService, ReceiveConfig descriptor, String name, String urlPath) {
		super(processorService, descriptor, name);
		this.urlPath = urlPath;
	}

	@Override
	public void start() {
		processorService.getHttpService().addPostBodyConsumer(urlPath, this);
	}

	@Override
	public void stop() {
		processorService.getHttpService().removePostBodyConsumer(urlPath, this);
	}

	@Override
	public void handlePost(Object input) {
		processorService.withExceptionHandling("error consuming http post",
				new ProcessorVoidExecutionContext(this),
				() -> {
					Payload<?> remotePayload = processorService.getObjectMapper().convertValue(input, Payload.class);
					outputTransformed(remotePayload.getValue(), remotePayload);
				});
	}

}

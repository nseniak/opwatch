package org.opwatch.processor.primitives.producer.receive;

import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.Producer;
import org.opwatch.service.HttpService;
import org.opwatch.service.ProcessorService;

public class Receive extends Producer<ReceiveConfig> implements HttpService.PostBodyHandle {

	private String urlPath;

	public Receive(ProcessorService processorService, ReceiveConfig configuration, String name, String urlPath) {
		super(processorService, configuration, name);
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
				() -> new ProcessorVoidExecutionScope(this),
				() -> {
					Payload<?> remotePayload = processorService.getObjectMapperService().objectMapper().convertValue(input, Payload.class);
					outputTransformed(remotePayload.getValue(), remotePayload);
				});
	}

}

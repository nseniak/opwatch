package org.opwatch.processor.primitives.producer.receive;

import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.Producer;
import org.opwatch.service.HttpService;
import org.opwatch.service.ProcessorService;

public class Receive extends Producer<ReceiveConfig> implements HttpService.PostBodyHandle {

	private String path;

	public Receive(ProcessorService processorService, ReceiveConfig configuration, String name, String path) {
		super(processorService, configuration, name);
		this.path = path;
	}

	@Override
	public void start() {
		processorService.getHttpService().addPostBodyConsumer(path, this);
	}

	@Override
	public void stop() {
		processorService.getHttpService().removePostBodyConsumer(path, this);
	}

	@Override
	public void handlePost(String input) {
		processorService.withExceptionHandling("error consuming http post",
				() -> new ProcessorVoidExecutionScope(this),
				() -> {
					Payload<?> remotePayload = processorService.getObjectMapperService().objectMapper().convertValue(input, Payload.class);
					outputTransformed(remotePayload.getValue(), remotePayload);
				});
	}

}

package org.opwatch.processor.primitives.consumer.send;

import org.opwatch.processor.common.ProcessorPayloadExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.consumer.Consumer;
import org.opwatch.service.ProcessorService;
import org.springframework.web.util.UriComponentsBuilder;

import static org.opwatch.service.HttpService.RECEIVE_PATH_PREFIX;

public class Send extends Consumer<SendConfig> {

	private String pathString;
	private String hostname;
	private int port;
	private String path;
	private String uri;

	public Send(ProcessorService processorService, SendConfig configuration, String name, String pathString, String hostname, int port, String path) {
		super(processorService, configuration, name);
		this.pathString = pathString;
		this.hostname = hostname;
		this.port = port;
		this.path = path;
		uri = UriComponentsBuilder.newInstance().scheme("http").host(hostname).port(port).path(RECEIVE_PATH_PREFIX + path).toUriString();
	}

	@Override
	public void consume(Payload<?> payload) {
		processorService.postForEntityWithErrors(uri, payload, Void.class, hostname, port, path,
				() -> new ProcessorPayloadExecutionScope(this, payload));
	}

}

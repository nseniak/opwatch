package com.untrackr.alerter.processor.primitives.consumer.send;

import com.untrackr.alerter.processor.common.ProcessorPayloadExecutionScope;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.primitives.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;
import org.springframework.web.util.UriComponentsBuilder;

import static com.untrackr.alerter.service.HttpService.RECEIVE_PATH_PREFIX;

public class Send extends Consumer<SendConfig> {

	private String pathString;
	private String hostname;
	private int port;
	private String path;
	private String uri;

	public Send(ProcessorService processorService, SendConfig descriptor, String name, String pathString, String hostname, int port, String path) {
		super(processorService, descriptor, name);
		this.pathString = pathString;
		this.hostname = hostname;
		this.port = port;
		this.path = path;
		uri = UriComponentsBuilder.newInstance().scheme("http").host(hostname).port(port).path(RECEIVE_PATH_PREFIX + path).toUriString();
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		processorService.postForEntityWithErrors(uri, payload, Void.class, hostname, port, path,
				() -> new ProcessorPayloadExecutionScope(this, payload));
	}

}

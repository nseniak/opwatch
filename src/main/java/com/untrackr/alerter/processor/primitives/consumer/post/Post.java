package com.untrackr.alerter.processor.primitives.consumer.post;

import com.untrackr.alerter.processor.common.ProcessorPayloadExecutionScope;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.processor.payload.RemotePayload;
import com.untrackr.alerter.processor.primitives.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class Post extends Consumer<PostConfig> {

	private String pathString;
	private String hostname;
	private int port;
	private String urlPath;
	private String uri;
	private RestTemplate restTemplate = new RestTemplate();

	public Post(ProcessorService processorService, PostConfig descriptor, String name, String pathString, String hostname, int port, String urlPath) {
		super(processorService, descriptor, name);
		this.pathString = pathString;
		this.hostname = hostname;
		this.port = port;
		this.urlPath = urlPath;
		uri = UriComponentsBuilder.newInstance().scheme("http").host(hostname).port(port).path("/processor" + urlPath).toUriString();
	}

	@Override
	public void consumeInOwnThread(Payload<?> payload) {
		RemotePayload remotePayload = new RemotePayload(payload);
		try {
			restTemplate.postForEntity(uri, remotePayload, Void.class);
		} catch (HttpStatusCodeException e) {
			HttpStatus status = e.getStatusCode();
			String errorMessage = e.getResponseBodyAsString();
			if (errorMessage == null) {
				errorMessage = "bad status: " + status.value() + " " + status.getReasonPhrase();
			}
			throw new RuntimeError("invalid response status when posting to \"" + pathString + "\": " + errorMessage,
					new ProcessorPayloadExecutionScope(this, payload));
		}
	}

}

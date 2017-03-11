package com.untrackr.alerter.processor.consumer.post;

import com.untrackr.alerter.common.RemotePayload;
import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.consumer.Consumer;
import com.untrackr.alerter.service.ProcessorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class Post extends Consumer<PostDesc> {

	private String pathString;
	private String hostname;
	private int port;
	private String urlPath;
	private boolean postErrorSignaled = false;

	public Post(ProcessorService processorService, PostDesc descriptor, String name, String pathString, String hostname, int port, String urlPath) {
		super(processorService, descriptor, name);
		this.pathString = pathString;
		this.hostname = hostname;
		this.port = port;
		this.urlPath = urlPath;
	}

	@Override
	public void consume(Payload payload) {
		String uri = UriComponentsBuilder.newInstance().scheme("http").host(hostname).port(port).path("/processor" + urlPath).toUriString();
		RestTemplate restTemplate = new RestTemplate();
		RemotePayload remotePayload = new RemotePayload(payload);
		ResponseEntity<Void> response;
		try {
			response = restTemplate.postForEntity(uri, remotePayload, Void.class);
		} catch (RestClientException e) {
			if (postErrorSignaled) {
				return;
			} else {
				postErrorSignaled = true;
				throw new AlerterException("http error when posting to \"" + pathString + "\": " + e.getLocalizedMessage(),
						ExceptionContext.makeProcessorPayload(this, payload));
			}
		}
		HttpStatus status = response.getStatusCode();
		if (status != HttpStatus.OK) {
			postErrorSignaled = true;
			throw new AlerterException("invalid response status when posting to \"" + pathString + "\": " + status.value() + " " + status.getReasonPhrase(),
					ExceptionContext.makeProcessorPayload(this, payload));
		}
		postErrorSignaled = false;
	}

}

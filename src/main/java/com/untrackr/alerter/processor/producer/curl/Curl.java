package com.untrackr.alerter.processor.producer.curl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.untrackr.alerter.common.ScriptObject;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

public class Curl extends ScheduledProducer {

	private URI uri;
	private int connectTimeout;
	private int readTimeout;

	private ObjectMapper objectMapper = new ObjectMapper();
	private MediaType anyText = MediaType.valueOf("text/*");

	public Curl(ProcessorService processorService, IncludePath path, ScheduledExecutor scheduledExecutor, URI uri, int connectTimeout, int readTimeout) {
		super(processorService, path, scheduledExecutor);
		this.uri = uri;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}

	@Override
	protected void produce() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		RestTemplate template = new RestTemplate(factory);
		Response result = new Response(processorService);
		result.url = uri.toString();
		try {
			ResponseEntity<String> response = template.getForEntity(uri, String.class);
			result.status = response.getStatusCode().value();
			MediaType contentType = response.getHeaders().getContentType();
			result.contentType = contentType.toString();
			if (anyText.includes(contentType)) {
				result.text = response.getBody();
			}
			if (MediaType.APPLICATION_JSON.includes(contentType)) {
				try {
					Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
					result.json = jsonObject;
				} catch (IOException e) {
					// Leave it to null
				}
			}
		} catch (HttpStatusCodeException e) {
			result.status = e.getStatusCode().value();
			result.error = e.getStatusText();
		} catch (ResourceAccessException e) {
			result.status = -1;
			result.error = e.getCause().getMessage();
		}
		outputProduced(result);
	}

	@Override
	public String identifier() {
		return uri.toString();
	}

	public static class Response extends ScriptObject {

		private String url;
		private int status;
		private String contentType;
		private String text;
		private Object json;
		private String error;

		public Response(ProcessorService processorService) {
			super(processorService);
		}

		public String getUrl() {
			return url;
		}

		public int getStatus() {
			return status;
		}

		public String getContentType() {
			return contentType;
		}

		public String getText() {
			return text;
		}

		public Object getJson() {
			return json;
		}

		public String getError() {
			return error;
		}

	}

}

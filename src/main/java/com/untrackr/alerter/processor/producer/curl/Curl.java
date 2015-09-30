package com.untrackr.alerter.processor.producer.curl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
	protected Object produce() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		RestTemplate template = new RestTemplate(factory);
		Response result = new Response();
		result.setUrl(uri.toString());
		try {
			ResponseEntity<String> response = template.getForEntity(uri, String.class);
			result.setStatus(response.getStatusCode().value());
			MediaType contentType = response.getHeaders().getContentType();
			result.setContentType(contentType.toString());
			if (anyText.includes(contentType)) {
				result.setText(response.getBody());
			}
			if (MediaType.APPLICATION_JSON.includes(contentType)) {
				try {
					Object jsonObject = objectMapper.readValue(response.getBody(), Object.class);
					result.setJson(jsonObject);
				} catch (IOException e) {
					// Leave it to null
				}
			}
		} catch (HttpStatusCodeException e) {
			result.setStatus(e.getStatusCode().value());
			result.setError(e.getStatusText());
		} catch (ResourceAccessException e) {
			result.setStatus(-1);
			result.setError(e.getCause().getMessage());
		}
		return result;
	}

	@Override
	public String identifier() {
		return uri.toString();
	}

	public static class Response {

		private String url;
		private int status;
		private String contentType;
		private String text;
		private Object json;
		private String error;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public int getStatus() {
			return status;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public Object getJson() {
			return json;
		}

		public void setJson(Object json) {
			this.json = json;
		}

		public String getError() {
			return error;
		}

		public void setError(String error) {
			this.error = error;
		}

	}

}

package org.opwatch.processor.primitives.producer.curl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opwatch.processor.payload.PayloadObjectValue;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

public class Curl extends ScheduledProducer<CurlConfig> {

	private final boolean insecure;
	private URI uri;
	private int connectTimeout;
	private int readTimeout;

	private ObjectMapper objectMapper = new ObjectMapper();
	private MediaType anyText = MediaType.valueOf("text/*");

	public Curl(ProcessorService processorService,
							CurlConfig configuration,
							String name,
							ScheduledExecutor scheduledExecutor,
							URI uri,
							int connectTimeout,
							int readTimeout,
							boolean insecure) {
		super(processorService, configuration, name, scheduledExecutor);
		this.uri = uri;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.insecure = insecure;
	}

	private static HostnameVerifier nullVerifier = (s, sslSession) -> true;

	private SimpleClientHttpRequestFactory makeInsecureClientHttpRequestFactory() {
		return new SimpleClientHttpRequestFactory() {
			@Override
			protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
				if (connection instanceof HttpsURLConnection) {
					((HttpsURLConnection) connection).setHostnameVerifier(nullVerifier);
				}
				super.prepareConnection(connection, httpMethod);
			}
		};
	}

	@Override
	protected void produce() {
		SimpleClientHttpRequestFactory factory = insecure ? makeInsecureClientHttpRequestFactory() : new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		RestTemplate template = new RestTemplate(factory);
		Response result = new Response();
		result.url = uri.toString();
		try {
			ResponseEntity<String> response = template.getForEntity(result.url, String.class);
			result.status = response.getStatusCode().value();
			MediaType contentType = response.getHeaders().getContentType();
			if (contentType != null) {
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

	public static class Response extends PayloadObjectValue {

		private String url;
		private int status;
		private String contentType;
		private String text;
		private Object json;
		private String error;

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

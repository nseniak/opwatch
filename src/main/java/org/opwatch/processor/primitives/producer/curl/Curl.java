/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.processor.primitives.producer.curl;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.processor.common.SchedulingInfo;
import org.opwatch.processor.config.ValueOrList;
import org.opwatch.processor.payload.PayloadHeadersValue;
import org.opwatch.processor.payload.PayloadPojoValue;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class Curl extends ScheduledProducer<CurlConfig> {

	private URI uri;
	private HttpMethod method;
	private Map<String, ValueOrList<String>> headers;
	private Object data;
	private int connectTimeout;
	private int readTimeout;
	private boolean insecure;
	private boolean followRedirects;
	private int maxRedirects;

	private MediaType anyText = MediaType.valueOf("text/*");
	private SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();

	public Curl(ProcessorService processorService,
							CurlConfig configuration,
							String name,
							SchedulingInfo schedulingInfo,
							URI uri,
							HttpMethod method,
							Map<String, ValueOrList<String>> headers,
							Object data,
							int connectTimeout,
							int readTimeout,
							boolean insecure,
							boolean followRedirects,
							int maxRedirects) {
		super(processorService, configuration, name, schedulingInfo);
		this.uri = uri;
		this.method = method;
		this.headers = headers;
		this.data = data;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.insecure = insecure;
		this.followRedirects = followRedirects;
		this.maxRedirects = maxRedirects;
	}

	@Override
	protected void produce() {
		exchange(uri, 0);
	}

	private void exchange(URI exchangeUri, int redirects) {
		SimpleClientHttpRequestFactory factory = new CurlHttpRequestFactory(true, insecure);
		factory.setTaskExecutor(executor);
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		AsyncRestTemplate template = new AsyncRestTemplate(factory);
		Response result = new Response();
		result.url = uri.toString();
		if (!exchangeUri.equals(uri)) {
			result.redirectUrl = exchangeUri.toString();
		}
		String body = null;
		HttpHeaders requestHeaders = requestHeaders(headers);
		if (data != ScriptRuntime.UNDEFINED) {
			if (requestHeaders.getContentType() == null) {
				requestHeaders.setContentType(APPLICATION_JSON);
				body = processorService.getScriptService().jsonStringify(data);
			} else if (requestHeaders.getContentType().includes(APPLICATION_JSON)) {
				body = processorService.getScriptService().jsonStringify(data);
			} else if (data != null) {
				body = data.toString();
			}
		}
		HttpEntity<String> request = new HttpEntity<>(body, requestHeaders);
		ListenableFuture<ResponseEntity<String>> listenableFuture = template.exchange(exchangeUri, method, request, String.class);
		listenableFuture.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
			@Override
			public void onFailure(Throwable ex) {
				if (ex instanceof HttpStatusCodeException) {
					HttpStatusCodeException e = (HttpStatusCodeException) ex;
					result.status = e.getStatusCode().value();
					result.statusDescription = e.getStatusCode().getReasonPhrase();
				} else if (ex instanceof ResourceAccessException) {
					ResourceAccessException e = (ResourceAccessException) ex;
					result.status = -1;
					result.error = "I/O error: " + e.getCause().getMessage();
				} else if (ex instanceof UnknownHostException) {
					result.status = -1;
					result.error = "unknown host: " + ex.getMessage();
				} else {
					result.status = -1;
					result.error = ex.getMessage();
				}
				if (running()) {
					outputProduced(result.toJavascript(processorService.getScriptService()));
				}
			}

			@Override
			public void onSuccess(ResponseEntity<String> response) {
				if (followRedirects && response.getStatusCode().is3xxRedirection()) {
					if (redirects == maxRedirects) {
						result.status = -1;
						result.error = "too many redirects";
						if (running()) {
							outputProduced(result.toJavascript(processorService.getScriptService()));
						}
						return;
					} else {
						URI redirectLocation = response.getHeaders().getLocation();
						if (redirectLocation != null) {
							exchange(redirectLocation, redirects + 1);
							return;
						}
					}
				}
				result.status = response.getStatusCode().value();
				MediaType contentType = response.getHeaders().getContentType();
				result.headers = new PayloadHeadersValue(response.getHeaders());
				if (contentType != null) {
					if (anyText.includes(contentType)) {
						result.text = response.getBody();
					}
					if (APPLICATION_JSON.includes(contentType)) {
						try {
							result.json = processorService.getScriptService().jsonParse(response.getBody());
						} catch (Throwable e) {
							// Leave it to null
						}
					}
				}
				if (running()) {
					outputProduced(result.toJavascript(processorService.getScriptService()));
				}
			}
		});
	}

	private HttpHeaders requestHeaders(Map<String, ValueOrList<String>> map) {
		HttpHeaders result = new HttpHeaders();
		for (Map.Entry<String, ValueOrList<String>> entry : map.entrySet()) {
			String key = entry.getKey();
			ValueOrList<String> vol = entry.getValue();
			if (vol.getType() == ValueOrList.Type.value) {
				result.add(key, vol.getValue());
			} else {
				for (String value : vol.getList()) {
					result.add(key, value);
				}
			}
		}
		return result;
	}

	private static HostnameVerifier nullVerifier = (s, sslSession) -> true;

	private static class CurlHttpRequestFactory extends SimpleClientHttpRequestFactory {

		private boolean followRedirects;
		private boolean inscure;

		public CurlHttpRequestFactory(boolean followRedirects, boolean inscure) {
			this.followRedirects = followRedirects;
			this.inscure = inscure;
		}

		@Override
		protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
			if (inscure && (connection instanceof HttpsURLConnection)) {
				((HttpsURLConnection) connection).setHostnameVerifier(nullVerifier);
			}
			super.prepareConnection(connection, httpMethod);
			connection.setInstanceFollowRedirects(followRedirects);
		}

	}

	public static class Response extends PayloadPojoValue {

		private String url;
		private Object redirectUrl;
		private int status;
		private Object statusDescription = ScriptRuntime.UNDEFINED;
		private Object error = ScriptRuntime.UNDEFINED;
		private Object headers = ScriptRuntime.UNDEFINED;
		private Object text = ScriptRuntime.UNDEFINED;
		private Object json = ScriptRuntime.UNDEFINED;

		public String getUrl() {
			return url;
		}

		public Object getRedirectUrl() {
			return redirectUrl;
		}

		public int getStatus() {
			return status;
		}

		public Object getStatusDescription() {
			return statusDescription;
		}

		public Object getError() {
			return error;
		}

		public Object getHeaders() {
			return headers;
		}

		public Object getText() {
			return text;
		}

		public Object getJson() {
			return json;
		}

	}

}

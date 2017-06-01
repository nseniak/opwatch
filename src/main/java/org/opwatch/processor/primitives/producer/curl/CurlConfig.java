package org.opwatch.processor.primitives.producer.curl;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.processor.config.*;
import org.opwatch.service.Config;

import java.util.LinkedHashMap;
import java.util.Map;

public class CurlConfig extends ScheduledProcessorConfig {

	private String url;
	private String method = "GET";
	private Duration connectTimeout = Config.defaultHttpConnectTimeout();
	private Duration timeout = Config.defaultHttpReadTimeout();
	private Boolean insecure = false;
	private Integer maxRedirects = Config.defaultMaxRedirects();
	private Boolean followRedirects = Config.defaultFollowRedirects();
	private Map<String, ValueOrList<String>> headers = new LinkedHashMap<>();
	private Object data = ScriptRuntime.UNDEFINED;

	@ImplicitProperty
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@OptionalProperty
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@OptionalProperty
	public Duration getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	@OptionalProperty
	public Duration getTimeout() {
		return timeout;
	}

	public void setTimeout(Duration timeout) {
		this.timeout = timeout;
	}

	@OptionalProperty
	public Boolean getInsecure() {
		return insecure;
	}

	public void setInsecure(Boolean insecure) {
		this.insecure = insecure;
	}

	@OptionalProperty
	public Integer getMaxRedirects() {
		return maxRedirects;
	}

	public void setMaxRedirects(Integer maxRedirects) {
		this.maxRedirects = maxRedirects;
	}

	@OptionalProperty
	public Boolean getFollowRedirects() {
		return followRedirects;
	}

	public void setFollowRedirects(Boolean followRedirects) {
		this.followRedirects = followRedirects;
	}

	@OptionalProperty
	public Map<String, ValueOrList<String>> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, ValueOrList<String>> headers) {
		this.headers = headers;
	}

	@OptionalProperty
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}

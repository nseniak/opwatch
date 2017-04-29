package org.opwatch.processor.primitives.producer.curl;

import org.opwatch.processor.config.Duration;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.OptionalProperty;
import org.opwatch.processor.config.ScheduledProcessorConfig;
import org.opwatch.service.Config;

public class CurlConfig extends ScheduledProcessorConfig {

	private String url;
	private Duration connectTimeout = Config.defaultHttpConnectTimeout();
	private Duration readTimeout = Config.defaultHttpReadTimeout();
	private Boolean insecure = false;

	@ImplicitProperty
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@OptionalProperty
	public Duration getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	@OptionalProperty
	public Duration getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Duration readTimeout) {
		this.readTimeout = readTimeout;
	}

	@OptionalProperty
	public Boolean getInsecure() {
		return insecure;
	}

	public void setInsecure(Boolean insecure) {
		this.insecure = insecure;
	}

}

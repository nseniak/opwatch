package com.untrackr.alerter.processor.primitives.producer.curl;

import com.untrackr.alerter.processor.config.Duration;
import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.OptionalProperty;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;
import com.untrackr.alerter.service.AlerterConfig;

public class CurlConfig extends ScheduledProcessorConfig {

	private String url;
	private Duration connectTimeout = AlerterConfig.defaultHttpConnectTimeout();
	private Duration readTimeout = AlerterConfig.defaultHttpReadTimeout();
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

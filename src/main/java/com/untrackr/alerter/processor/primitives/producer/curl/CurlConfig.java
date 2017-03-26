package com.untrackr.alerter.processor.primitives.producer.curl;

import com.untrackr.alerter.processor.config.ImplicitProperty;
import com.untrackr.alerter.processor.config.OptionalProperty;
import com.untrackr.alerter.processor.config.ScheduledProcessorConfig;
import com.untrackr.alerter.service.AlerterProfile;

public class CurlConfig extends ScheduledProcessorConfig {

	private String url;
	private String connectTimeout = AlerterProfile.defaultHttpConnectTimeout();
	private String readTimeout = AlerterProfile.defaultHttpReadTimeout();
	private Boolean insecure = false;

	@ImplicitProperty
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@OptionalProperty
	public String getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(String connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	@OptionalProperty
	public String getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(String readTimeout) {
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
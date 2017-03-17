package com.untrackr.alerter.processor.producer.curl;

import com.untrackr.alerter.processor.common.ScheduledProducerDesc;

public class CurlDesc extends ScheduledProducerDesc {

	private String url;
	private String connectTimeout;
	private String readTimeout;
	private Boolean insecure = false;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(String connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(String readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Boolean getInsecure() {
		return insecure;
	}

	public void setInsecure(Boolean insecure) {
		this.insecure = insecure;
	}

}

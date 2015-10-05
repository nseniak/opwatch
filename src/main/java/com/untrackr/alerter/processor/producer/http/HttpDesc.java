package com.untrackr.alerter.processor.producer.http;

import com.untrackr.alerter.processor.common.ActiveProcessorDesc;

public class HttpDesc extends ActiveProcessorDesc {

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

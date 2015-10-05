package com.untrackr.alerter.processor.producer.receive;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.producer.Producer;
import com.untrackr.alerter.service.HttpService;
import com.untrackr.alerter.service.ProcessorService;

public class Receive extends Producer implements HttpService.PostBodyConsumer {

	private String urlPath;

	public Receive(ProcessorService processorService, IncludePath path, String urlPath) {
		super(processorService, path);
		this.urlPath = urlPath;
	}

	@Override
	public void doStart() {
		processorService.getHttpService().addPostBodyConsumer(urlPath, this);
	}

	@Override
	protected void doStop() {
		processorService.getHttpService().removePostBodyConsumer(urlPath, this);
	}

	@Override
	public void consume(Object line) {
		outputProduced(line);
	}

}

package com.untrackr.alerter.processor.filter.once;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

import java.time.Duration;

public class Once extends Filter {

	private long delay;
	private long lastInputTimestamp = 0;

	public Once(ProcessorService processorService, IncludePath path, long delay) {
		super(processorService, path);
		this.delay = delay;
	}

	@Override
	public void consume(Payload payload) {
		if ((payload.getTimestamp() - lastInputTimestamp) > delay) {
			outputFiltered(payload.getJsonObject(), payload);
		}
		lastInputTimestamp = payload.getTimestamp();
	}

	@Override
	public String identifier() {
		return Duration.ofMillis(delay).toString();
	}

}

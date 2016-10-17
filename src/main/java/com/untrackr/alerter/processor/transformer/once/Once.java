package com.untrackr.alerter.processor.transformer.once;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

import java.time.Duration;

public class Once extends Transformer {

	private long delay;
	private long lastInputTimestamp = 0;

	public Once(ProcessorService processorService, ScriptStack stack, long delay) {
		super(processorService, stack);
		this.delay = delay;
	}

	@Override
	public void consume(Payload payload) {
		if ((payload.getTimestamp() - lastInputTimestamp) > delay) {
			outputTransformed(payload.getScriptObject(), payload);
		}
		lastInputTimestamp = payload.getTimestamp();
	}

	@Override
	public String identifier() {
		return Duration.ofMillis(delay).toString();
	}

}

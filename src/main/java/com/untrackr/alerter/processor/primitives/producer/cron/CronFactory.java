package com.untrackr.alerter.processor.primitives.producer.cron;

import com.untrackr.alerter.processor.primitives.producer.CommandRunner;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class CronFactory extends ScheduledExecutorFactory<CronDescriptor, Cron> {

	public CronFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "cron";
	}

	@Override
	public Class<CronDescriptor> descriptorClass() {
		return CronDescriptor.class;
	}

	@Override
	public Cron make(Object scriptObject) {
		CronDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		CommandRunner runner = makeCommandOutputProducer(descriptor);
		Cron cron = new Cron(getProcessorService(), descriptor, type(), executor, runner);
		return cron;
	}

}

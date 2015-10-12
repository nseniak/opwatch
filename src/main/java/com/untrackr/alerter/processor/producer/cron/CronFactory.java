package com.untrackr.alerter.processor.producer.cron;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class CronFactory extends ScheduledExecutorFactory {

	public CronFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "cron";
	}

	@Override
	public Cron make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		CronDesc descriptor = convertDescriptor(path, CronDesc.class, jsonDescriptor);
		ScheduledExecutor executor = makeScheduledExecutor(path, jsonDescriptor, descriptor);
		CommandRunner runner = makeCommandOutputProducer(path, jsonDescriptor, descriptor);
		Cron cron = new Cron(getProcessorService(), path, executor, runner);
		initialize(cron, descriptor);
		return cron;
	}

}

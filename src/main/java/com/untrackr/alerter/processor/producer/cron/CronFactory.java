package com.untrackr.alerter.processor.producer.cron;

import com.untrackr.alerter.processor.consumer.alert.AlertGeneratorDesc;
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
	public Class<CronDesc> descriptorClass() {
		return CronDesc.class;
	}

	@Override
	public Cron make(Object scriptObject) {
		CronDesc descriptor = convertProcessorDescriptor(CronDesc.class, scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		CommandRunner runner = makeCommandOutputProducer(descriptor);
		Cron cron = new Cron(getProcessorService(), descriptor, type(), executor, runner);
		return cron;
	}

}

package com.untrackr.alerter.processor.producer.cron;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
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
	public String name() {
		return "cron";
	}

	@Override
	public Processor make(Object scriptObject) throws ValidationError {
		CronDesc descriptor = convertProcessorArgument(CronDesc.class, scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		CommandRunner runner = makeCommandOutputProducer(descriptor);
		Cron cron = new Cron(getProcessorService(), ScriptStack.currentStack(), executor, runner);
		initialize(cron, descriptor);
		return cron;
	}

}

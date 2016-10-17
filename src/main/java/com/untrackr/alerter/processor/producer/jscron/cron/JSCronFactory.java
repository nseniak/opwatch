package com.untrackr.alerter.processor.producer.jscron.cron;

import com.untrackr.alerter.processor.common.JavascriptGenerator;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class JSCronFactory extends ScheduledExecutorFactory {

	public JSCronFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "jscron";
	}

	@Override
	public Processor make(Object scriptObject) throws ValidationError {
		JSCronDesc descriptor = convertProcessorArgument(JSCronDesc.class, scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(descriptor);
		JavascriptGenerator generator = checkFieldValue("generator", descriptor.getGenerator());
		JSCron JSCron = new JSCron(getProcessorService(), ScriptStack.currentStack(), executor, generator);
		initialize(JSCron, descriptor);
		return JSCron;
	}

}

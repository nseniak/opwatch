package com.untrackr.alerter.processor.producer.jscron.cron;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.*;
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
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		JSCronDesc descriptor = convertScriptDescriptor(JSCronDesc.class, jsonDescriptor);
		ScheduledExecutor executor = makeScheduledExecutor(jsonDescriptor, descriptor);
		JavascriptGenerator generator = checkFieldValue(jsonDescriptor, "generator", descriptor.getGenerator());
		JSCron JSCron = new JSCron(getProcessorService(), ScriptStack.currentStack(), executor, generator);
		initialize(JSCron, descriptor);
		return JSCron;
	}

}

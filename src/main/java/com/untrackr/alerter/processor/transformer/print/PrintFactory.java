package com.untrackr.alerter.processor.transformer.print;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.service.ProcessorService;

public class PrintFactory extends ActiveProcessorFactory {

	public PrintFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "echo";
	}

	@Override
	public Processor make(Object scriptObject) {
		PrintDesc descriptor = convertProcessorArgument(PrintDesc.class, scriptObject);
		Print print = new Print(getProcessorService(), displayName(descriptor));
		return print;
	}

}

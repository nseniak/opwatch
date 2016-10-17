package com.untrackr.alerter.processor.filter.print;

import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.filter.Filter;
import com.untrackr.alerter.service.ProcessorService;

public class Echo extends Filter {

	public Echo(ProcessorService processorService, ScriptStack stack) {
		super(processorService, stack);
		// Override signature
		this.signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.required, ProcessorSignature.PipeRequirement.any);
	}

	@Override
	public void consume(Payload input) {
		System.out.println(input.asText());
		outputFiltered(input.getScriptObject(), input);
	}

}

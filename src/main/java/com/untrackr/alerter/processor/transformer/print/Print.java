package com.untrackr.alerter.processor.transformer.print;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Print extends Transformer {

	public Print(ProcessorService processorService, String name) {
		super(processorService, name);
		// Override signature
		this.signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.required, ProcessorSignature.PipeRequirement.any);
	}

	@Override
	public void consume(Payload input) {
		System.out.println(input.asText());
		outputTransformed(input.getScriptObject(), input);
	}

}

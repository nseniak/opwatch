package com.untrackr.alerter.processor.transformer.print;

import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.transformer.Transformer;
import com.untrackr.alerter.service.ProcessorService;

public class Stdout extends Transformer {

	public Stdout(ProcessorService processorService, StdoutDesc descriptor, String name) {
		super(processorService, descriptor, name);
		// Override signature
		this.signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.required, ProcessorSignature.PipeRequirement.any);
	}

	@Override
	public void consume(Payload input) {
		System.out.println(processorService.json(input));
		outputTransformed(input.getValue(), input);
	}

}

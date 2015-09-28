package com.untrackr.alerter.processor.filter;

import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.service.ProcessorService;

public class Print extends ActiveProcessor {

	public Print(ProcessorService processorService, IncludePath path) {
		super(processorService, path);
		this.signature = new ProcessorSignature(ProcessorSignature.PipeRequirement.required, ProcessorSignature.PipeRequirement.any);
	}

	@Override
	public void initialize() {
		// Do nothing
	}

	@Override
	public void consume(Payload input) {
		System.out.println(input.asText());
		outputFiltered(input.getJsonObject(), input);
	}

}

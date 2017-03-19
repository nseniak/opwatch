package com.untrackr.alerter.processor.common;

public class ProcessorSignature {

	/**
	 * 					Any
	 * 					/	\
	 * 			Data	Void
	 * 				 \  /
	 * 				 None
	 */
	public enum PipeRequirement {
		None,
		// For input: means that the processor is not a consumer
		// For output: means that the processor is not a producer
		NoData,
		// For input: means that the processor is a consumer
		// For output: means that the processor is a producer
		Data,
		// For input: means that the processor can be a consumer, but doesn't necessarily require data
		// For output: means that the processor is a producer but also performs side effects so its output can be
		// ignored
		Any
	}

	private PipeRequirement inputRequirement;
	private PipeRequirement outputRequirement;

	public ProcessorSignature(PipeRequirement inputRequirement, PipeRequirement outputRequirement) {
		this.inputRequirement = inputRequirement;
		this.outputRequirement = outputRequirement;
	}

	public static ProcessorSignature makeProducer() {
		return new ProcessorSignature(PipeRequirement.NoData, PipeRequirement.Data);
	}

	public static ProcessorSignature makeConsumer() {
		return new ProcessorSignature(PipeRequirement.Data, PipeRequirement.NoData);
	}

	public static ProcessorSignature makeSideEffectConsumer() {
		return new ProcessorSignature(PipeRequirement.Data, PipeRequirement.Any);
	}

	public static ProcessorSignature makeTransformer() {
		return new ProcessorSignature(PipeRequirement.Data, PipeRequirement.Data);
	}

	public static ProcessorSignature makeAny() {
		return new ProcessorSignature(PipeRequirement.Any, PipeRequirement.Any);
	}

	public static PipeRequirement bottom(PipeRequirement req1, PipeRequirement req2) {
		if (req1 == req2) {
			return req1;
		} if (req1 == PipeRequirement.Any) {
			return req2;
		} else if (req2 == PipeRequirement.Any) {
			return req1;
		} else {
			return PipeRequirement.None;
		}
	}

	public ProcessorSignature bottom(ProcessorSignature other) {
		PipeRequirement inputReq = bottom(inputRequirement, other.getInputRequirement());
		PipeRequirement outputReq = bottom(outputRequirement, other.getOutputRequirement());
		return new ProcessorSignature(inputReq, outputReq);
	}

	public String describe() {
		StringBuilder builder = new StringBuilder();
		builder.append("{input='");
		switch (inputRequirement) {
			case Data:
				builder.append("required");
				break;
			case NoData:
				builder.append("ignored");
				break;
			case Any:
				builder.append("optional");
				break;
		}
		builder.append("', output='");
		switch (outputRequirement) {
			case Data:
				builder.append("required");
				break;
			case NoData:
				builder.append("none");
				break;
			case Any:
				builder.append("optional");
				break;
		}
		builder.append("'}");
		return builder.toString();
	}

	public PipeRequirement getInputRequirement() {
		return inputRequirement;
	}

	public void setInputRequirement(PipeRequirement inputRequirement) {
		this.inputRequirement = inputRequirement;
	}

	public PipeRequirement getOutputRequirement() {
		return outputRequirement;
	}

	public void setOutputRequirement(PipeRequirement outputRequirement) {
		this.outputRequirement = outputRequirement;
	}

}

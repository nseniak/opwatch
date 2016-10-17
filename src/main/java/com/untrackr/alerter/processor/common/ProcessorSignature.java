package com.untrackr.alerter.processor.common;

public class ProcessorSignature {

	public enum PipeRequirement {
		required, forbidden, any
	}

	private PipeRequirement inputRequirement;
	private PipeRequirement outputRequirement;

	public ProcessorSignature(PipeRequirement inputRequirement, PipeRequirement outputRequirement) {
		this.inputRequirement = inputRequirement;
		this.outputRequirement = outputRequirement;
	}

	public static ProcessorSignature makeProducer() {
		return new ProcessorSignature(PipeRequirement.forbidden, PipeRequirement.required);
	}

	public static ProcessorSignature makeConsumer() {
		return new ProcessorSignature(PipeRequirement.required, PipeRequirement.forbidden);
	}

	public static ProcessorSignature makeTransformer() {
		return new ProcessorSignature(PipeRequirement.required, PipeRequirement.required);
	}

	public static PipeRequirement bottom(PipeRequirement req1, PipeRequirement req2) {
		if (req1 == PipeRequirement.any) {
			return req2;
		} else if (req2 == PipeRequirement.any) {
			return req1;
		} else if (req1 == req2) {
			return req1;
		} else {
			// One of req1 or req2 is required
			return null;
		}
	}

	public ProcessorSignature bottom(ProcessorSignature other) {
		PipeRequirement inputReq = bottom(inputRequirement, other.getInputRequirement());
		PipeRequirement outputReq = bottom(outputRequirement, other.getOutputRequirement());
		if ((inputReq == null) || (outputReq == null)) {
			return null;
		} else {
			return new ProcessorSignature(inputReq, outputReq);
		}
	}

	public String describe() {
		StringBuilder builder = new StringBuilder();
		builder.append("{input='");
		switch (inputRequirement) {
			case required:
				builder.append("required");
				break;
			case forbidden:
				builder.append("ignored");
				break;
			case any:
				builder.append("optional");
				break;
		}
		builder.append("', output='");
		switch (outputRequirement) {
			case required:
				builder.append("required");
				break;
			case forbidden:
				builder.append("none");
				break;
			case any:
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

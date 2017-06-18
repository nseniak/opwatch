/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.processor.common;

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

	public static ProcessorSignature makeSideEffectFilter() {
		return new ProcessorSignature(PipeRequirement.Data, PipeRequirement.Any);
	}

	public static ProcessorSignature makeFilter() {
		return new ProcessorSignature(PipeRequirement.Data, PipeRequirement.Data);
	}

	public static ProcessorSignature makeProducerOrFilter() {
		return new ProcessorSignature(PipeRequirement.Any, PipeRequirement.Data);
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

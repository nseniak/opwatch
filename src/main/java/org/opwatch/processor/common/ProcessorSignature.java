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

import java.util.StringJoiner;

public class ProcessorSignature {

	public enum DataRequirement {
		None,
		// As input: Should not receive any input data
		// As output: Does not produces any data
		NoData,
		// As input: Requires input data
		// As output: Produces data that should not be ignored
		Data,
		// As input: Can receive data but does not require it
		// As output: Produces data but that data can be ignored
		Any
	}

	private DataRequirement inputRequirement;
	private DataRequirement outputRequirement;

	public ProcessorSignature(DataRequirement inputRequirement, DataRequirement outputRequirement) {
		this.inputRequirement = inputRequirement;
		this.outputRequirement = outputRequirement;
	}

	public static ProcessorSignature makeProducer() {
		return new ProcessorSignature(DataRequirement.NoData, DataRequirement.Data);
	}

	public static ProcessorSignature makeConsumer() {
		return new ProcessorSignature(DataRequirement.Data, DataRequirement.NoData);
	}

	public static ProcessorSignature makeSideEffectFilter() {
		return new ProcessorSignature(DataRequirement.Data, DataRequirement.Any);
	}

	public static ProcessorSignature makeFilter() {
		return new ProcessorSignature(DataRequirement.Data, DataRequirement.Data);
	}

	public static ProcessorSignature makeProducerOrFilter() {
		return new ProcessorSignature(DataRequirement.Any, DataRequirement.Data);
	}

	public static ProcessorSignature makeAny() {
		return new ProcessorSignature(DataRequirement.Any, DataRequirement.Any);
	}

	public void checkInputCompatibility(StringJoiner errors, DataRequirement input) {
		switch (input) {
			case NoData:
				if (inputRequirement == DataRequirement.Data) {
					errors.add("input is required");
				}
				break;
			case Data:
			case Any:
				if (inputRequirement == DataRequirement.NoData) {
					errors.add("cannot receive an input");
				}
				break;
		}
	}

	public void checkOutputCompatibility(StringJoiner errors, DataRequirement output) {
		switch (outputRequirement) {
			case NoData:
				if (output == DataRequirement.Data) {
					errors.add("does not generate an output");
				}
				break;
			case Data:
				if (output == DataRequirement.NoData) {
					errors.add("output should be used");
				}
				break;
			case Any:
				break;
		}
	}

	public static DataRequirement parallelRequirement(DataRequirement req1, DataRequirement req2) {
		// Data x Data -> Data
		// Data x NoData -> Data
		// Data x Any -> Data
		// NoData x Data -> Data
		// NoData x NoData -> NoData
		// NoData x Any -> Any
		// Any x Data -> Data
		// Any x NoData -> Any
		// Any x Any -> Any
		if ((req1 == DataRequirement.Data) || (req2 == DataRequirement.Data)) {
			return DataRequirement.Data;
		} if ((req1 == DataRequirement.Any) || (req2 == DataRequirement.Any)) {
			return DataRequirement.Any;
		} else {
			return DataRequirement.NoData;
		}
	}

	public ProcessorSignature parallel(ProcessorSignature other) {
		DataRequirement inputReq = parallelRequirement(inputRequirement, other.getInputRequirement());
		DataRequirement outputReq = parallelRequirement(outputRequirement, other.getOutputRequirement());
		return new ProcessorSignature(inputReq, outputReq);
	}

	public DataRequirement getInputRequirement() {
		return inputRequirement;
	}

	public void setInputRequirement(DataRequirement inputRequirement) {
		this.inputRequirement = inputRequirement;
	}

	public DataRequirement getOutputRequirement() {
		return outputRequirement;
	}

	public void setOutputRequirement(DataRequirement outputRequirement) {
		this.outputRequirement = outputRequirement;
	}

}

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

import org.opwatch.common.Assertion;

public class ProcessorSignature {

	private DataRequirement inputRequirement;
	private DataRequirement outputRequirement;

	public ProcessorSignature(DataRequirement inputRequirement, DataRequirement outputRequirement) {
		Assertion.assertExecutionState(inputRequirement != DataRequirement.Unknown);
		Assertion.assertExecutionState(outputRequirement != DataRequirement.Unknown);
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

	public static String inputCompatibilityError(DataRequirement previousOutput, DataRequirement input) {
		// Data x Unknown -> OK
		// Data x Data -> OK
		// Data x NoData -> Error
		// Data x Any -> OK
		// NoData x Unknown -> OK
		// NoData x Data -> Error
		// NoData x NoData -> OK
		// NoData x Any -> OK
		// Any x Unknown -> OK
		// Any x Data -> OK
		// Any x NoData -> OK
		// Any x Any -> OK
		// Unknown x Unknown -> OK
		// Unknown x Data -> OK
		// Unknown x NoData -> OK
		// Unknown x Any -> OK
		if ((previousOutput == DataRequirement.Data) && (input == DataRequirement.NoData)) {
			return "should not receive an input";
		} else if ((previousOutput == DataRequirement.NoData) && (input == DataRequirement.Data)) {
			return "should receive an input";
		} else {
			return null;
		}
	}

	public static String outputCompatibilityError(DataRequirement output, DataRequirement nextInput) {
		if ((output == DataRequirement.Data) && (nextInput == DataRequirement.NoData)) {
			return "generated output is lost";
		} else if ((output == DataRequirement.NoData) && (nextInput == DataRequirement.Data)) {
			return "does not generate an output";
		} else {
			return null;
		}
	}

	public static DataRequirement parallelOutput(DataRequirement req1, DataRequirement req2) {
		// Data x Unknown -> Data
		// Data x Data -> Data
		// Data x NoData -> Data
		// Data x Any -> Data
		// NoData x Unknown -> Unknown
		// NoData x Data -> Data
		// NoData x NoData -> NoData
		// NoData x Any -> Any
		// Any x Unknown -> Unknown
		// Any x Data -> Data
		// Any x NoData -> Any
		// Any x Any -> Any
		// Unknown x Unknown -> Unknown
		// Unknown x Data -> Data
		// Unknown x NoData -> Unknown
		// Unknown x Any -> Unknown
		if ((req1 == DataRequirement.Data) || (req2 == DataRequirement.Data)) {
			return DataRequirement.Data;
		} else if ((req1 == DataRequirement.Unknown) || (req2 == DataRequirement.Unknown)) {
			return DataRequirement.Unknown;
		}
		if ((req1 == DataRequirement.Any) || (req2 == DataRequirement.Any)) {
			return DataRequirement.Any;
		} else {
			return DataRequirement.NoData;
		}
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

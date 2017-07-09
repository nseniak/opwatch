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

public class InferenceResult {

	private Processor<?> processor;
	private String errorMessage;
	private DataRequirement requirement;

	private InferenceResult(Processor<?> processor, String errorMessage, DataRequirement requirement) {
		this.processor = processor;
		this.errorMessage = errorMessage;
		this.requirement = requirement;
	}

	public static InferenceResult makeError(Processor<?> processor, String errorMessage) {
		return new InferenceResult(processor, errorMessage, null);
	}

	public static InferenceResult makeRequirement(Processor<?> processor, DataRequirement requirement) {
		return new InferenceResult(processor, null, requirement);
	}

	public boolean isError() {
		return errorMessage != null;
	}

	public void throwError() {
		Assertion.assertExecutionState(errorMessage != null);
		throw new RuntimeError(errorMessage, new ProcessorVoidExecutionScope(processor));
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public DataRequirement getRequirement() {
		return requirement;
	}

	public void setRequirement(DataRequirement requirement) {
		this.requirement = requirement;
	}

}

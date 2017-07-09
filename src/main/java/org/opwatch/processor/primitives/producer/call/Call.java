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

package org.opwatch.processor.primitives.producer.call;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.processor.common.DataRequirement;
import org.opwatch.processor.common.InferenceResult;
import org.opwatch.processor.config.JavascriptConsumer;
import org.opwatch.processor.config.JavascriptProducer;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

import static org.opwatch.processor.common.ProcessorSignature.inputCompatibilityError;

public class Call extends ScheduledProducer<CallConfig> {

	private JavascriptConsumer input;
	private JavascriptProducer output;

	public Call(ProcessorService processorService, CallConfig configuration, String name, ScheduledExecutor scheduledExecutor,
							JavascriptConsumer input, JavascriptProducer output) {
		super(processorService, configuration, name, scheduledExecutor);
		this.input = input;
		this.output = output;
	}

	@Override
	public InferenceResult inferOutput(DataRequirement previousOutput) {
		DataRequirement inputRequirement = (input == null) ? DataRequirement.NoData : DataRequirement.Data;
		String errorMessage = inputCompatibilityError(previousOutput, inputRequirement);
		if (errorMessage == null) {
			return InferenceResult.makeRequirement(this, DataRequirement.Data);
		} else {
			return InferenceResult.makeError(this, errorMessage);
		}
	}

	@Override
	public void consume(Payload payload) {
		input.call(payload, this);
	}

	@Override
	protected void produce() {
		Object result = output.call(this);
		if (result != ScriptRuntime.UNDEFINED) {
			outputProduced(result);
		}
	}

}

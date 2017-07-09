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

import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.config.JavascriptConsumer;
import org.opwatch.processor.config.JavascriptProducer;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class CallFactory extends ScheduledExecutorFactory<CallConfig, Call> {

	public CallFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "call";
	}

	@Override
	public Class<CallConfig> configurationClass() {
		return CallConfig.class;
	}

	@Override
	public Class<Call> processorClass() {
		return Call.class;
	}

	@Override
	public Call make(Object scriptObject) {
		CallConfig config = convertProcessorConfig(scriptObject);
		ScheduledExecutor executor = makeScheduledExecutor(config, false);
		JavascriptConsumer input = config.getInput();
		JavascriptProducer output = checkPropertyValue("output", config.getOutput());
		return new Call(getProcessorService(), config, name(), executor, input, output);
	}

}

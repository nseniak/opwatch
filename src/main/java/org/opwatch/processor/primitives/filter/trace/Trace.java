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

package org.opwatch.processor.primitives.filter.trace;

import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.consumer.Consumer;
import org.opwatch.service.ProcessorService;
import org.opwatch.service.ScriptService;

public class Trace extends Consumer<TraceConfig> {

	public Trace(ProcessorService processorService, TraceConfig configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void consume(Payload payload) {
		ScriptService scriptService = processorService.getScriptService();
		processorService.printStdout(scriptService.pretty(payload.getValue()));
		outputTransformed(payload.getValue(), payload);
	}

}

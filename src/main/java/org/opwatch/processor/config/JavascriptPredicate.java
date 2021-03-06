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

package org.opwatch.processor.config;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ProcessorPayloadExecutionScope;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;
import org.opwatch.service.ScriptService;

public class JavascriptPredicate extends JavascriptFunction {

	public JavascriptPredicate(ScriptObjectMirror function, ValueLocation valueLocation, ProcessorService processorService) {
		super(function, valueLocation, processorService);
	}

	public boolean call(Payload payload, Processor processor) {
		Object result = invoke(processor, payload);
		ScriptService scriptService = processor.getProcessorService().getScriptService();
		return (boolean) scriptService.convertScriptValue(valueLocation, Boolean.class, result,
				(message) -> new RuntimeError(message, new ProcessorPayloadExecutionScope(processor, payload)));
	}

}

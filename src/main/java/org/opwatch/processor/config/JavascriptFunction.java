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

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.regex.Pattern;

public abstract class JavascriptFunction extends ConfigPropertyValue {

	protected ScriptObjectMirror function;
	protected ValueLocation valueLocation;
	protected ProcessorService processorService;

	protected JavascriptFunction(ScriptObjectMirror function, ValueLocation valueLocation, ProcessorService processorService) {
		this.function = function;
		this.valueLocation = valueLocation;
		this.processorService = processorService;
	}

	protected Object invoke(Processor processor, Payload payload) {
		synchronized (processorService.getScriptService()) {
			return function.call(function, payload.getValue(), payload);
		}
	}

	protected Object invoke(Processor processor) {
		synchronized (processorService.getScriptService()) {
			return function.call(function);
		}
	}

	@Override
	public String toString() {
		return Pattern.compile("\n[\t ]*", Pattern.DOTALL).matcher(function.toString()).replaceAll(" ");
	}

	public ValueLocation getValueLocation() {
		return valueLocation;
	}

	public ScriptObjectMirror getFunction() {
		return function;
	}

}

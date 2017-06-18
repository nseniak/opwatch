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

package org.opwatch.processor.primitives.filter.apply;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.processor.config.JavascriptFilter;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

public class Apply extends Filter<ApplyConfig> {

	private JavascriptFilter output;

	public Apply(ProcessorService processorService, ApplyConfig configuration, String name, JavascriptFilter output) {
		super(processorService, configuration, name);
		this.output = output;
	}

	@Override
	public void consume(Payload payload) {
		Object result = output.call(payload, this);
		if (result != ScriptRuntime.UNDEFINED) {
			outputTransformed(result, payload);
		}
	}

}

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

package org.opwatch.processor.primitives.filter.json;

import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ProcessorPayloadExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

import java.io.IOException;

public class Json extends Filter<JsonConfig> {

	public Json(ProcessorService processorService, JsonConfig configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void consume(Payload payload) {
		String value = payloadValue(payload, String.class);
		Object result = null;
		try {
			result = processorService.parseJson(value);
		} catch (IOException e) {
			throw new RuntimeError("cannot parse json: " + e.getMessage(),
					new ProcessorPayloadExecutionScope(this, payload),
					e);
		}
		outputTransformed(result, payload);
	}

}

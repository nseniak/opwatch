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

package org.opwatch.processor.payload;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Objects;

public class PayloadHeadersValue extends PayloadMapValue<HttpHeaders, List<String>> {

	public PayloadHeadersValue(HttpHeaders headers) {
		super(headers);
	}

	@Override
	public Object getMember(String name) {
		Objects.requireNonNull(name);
		if (map.containsKey(name)) {
			List<String> values = map.get(name);
			if (values.size() == 1) {
				return values.get(0);
			} else {
				return new PayloadArrayValue<>(values);
			}
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

}

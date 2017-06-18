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
import org.opwatch.service.ScriptService;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PayloadMapValue<M extends Map<String, T>, T> extends PayloadScriptValue {

	protected M map;

	public PayloadMapValue(M map) {
		this.map = map;
	}

	@Override
	public Object getSlot(int index) {
		return ScriptRuntime.UNDEFINED;
	}

	@Override
	public Object getMember(String name) {
		Objects.requireNonNull(name);
		if (map.containsKey(name)) {
			return map.get(name);
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

}

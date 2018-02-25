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

import java.util.List;
import java.util.Objects;

public class PayloadArrayValue<T> extends PayloadScriptValue {

	private List<T> list;

	public PayloadArrayValue(List<T> list) {
		this.list = list;
	}

	@Override
	public Object getSlot(int index) {
		if (hasSlot(index)) {
			return list.get(index);
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

	@Override
	public Object getMember(String name) {
		Objects.requireNonNull(name);
		if (name.equals("length")) {
			return list.size();
		} else if (name.equals("__payloadArray")) {
			return true;
		} else {
			return ScriptRuntime.UNDEFINED;
		}
	}

	@Override
	public boolean hasSlot(int slot) {
		return ((slot >= 0) && (slot < list.size()));
	}

	@Override
	public boolean isArray() {
		return true;
	}

}

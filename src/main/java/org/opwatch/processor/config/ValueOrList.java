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

import java.util.List;

public class ValueOrList<T> extends ConfigPropertyValue {

	public enum Type {
		value, list
	}

	private Type type;
	private T value;
	private List<T> list;

	private ValueOrList() {
	}

	public static <T> ValueOrList<T> makeValue(T value) {
		ValueOrList<T> vol = new ValueOrList<>();
		vol.type = Type.value;
		vol.value = value;
		return vol;
	}

	public static <T> ValueOrList makeList(List<T> list) {
		ValueOrList<T> vol = new ValueOrList<>();
		vol.type = Type.list;
		vol.list = list;
		return vol;
	}

	public Type getType() {
		return type;
	}

	public T getValue() {
		return value;
	}

	public List<T> getList() {
		return list;
	}

}

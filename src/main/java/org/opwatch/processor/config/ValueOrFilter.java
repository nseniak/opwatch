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
import org.opwatch.processor.common.ProcessorPayloadExecutionScope;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ValueLocation;
import org.opwatch.processor.payload.Payload;

public class ValueOrFilter<T> extends ConfigPropertyValue {

	public enum Type {
		value, filter
	}

	private Type type;
	private T value;
	private JavascriptFilter filter;

	private ValueOrFilter() {
	}

	public static <T> ValueOrFilter<T> makeValue(T value) {
		ValueOrFilter<T> vof = new ValueOrFilter();
		vof.type = Type.value;
		vof.value = value;
		return vof;
	}

	public static <T> ValueOrFilter<T> makeFunction(JavascriptFilter function) {
		ValueOrFilter<T> vof = new ValueOrFilter<>();
		vof.type = Type.filter;
		vof.filter = function;
		return vof;
	}

	public T value(Processor processor, Payload payload, Class<T> clazz) {
		if (type == Type.value) {
			return value;
		} else {
			Object value = filter.call(payload, processor);
			return (T) processor.getProcessorService().getScriptService().convertScriptValue(filter.getValueLocation(), clazz, value,
					(message) -> new RuntimeError(message, new ProcessorPayloadExecutionScope(processor, payload)));
		}
	}

	public T getValue() {
		return value;
	}

	public JavascriptFilter getFilter() {
		return filter;
	}

	public Type getType() {
		return type;
	}

}

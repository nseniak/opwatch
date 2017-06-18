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

package org.opwatch.processor.common;

import static org.opwatch.processor.common.ValueLocation.SourceType.property;

public class ValueLocation {

	public enum SourceType {
		property, list_property_element, argument, list_argument_element, toplevel
	}

	private SourceType sourceType;
	private String functionName;
	private String propertyName;

	private ValueLocation(SourceType sourceType, String functionName, String propertyName) {
		this.sourceType = sourceType;
		this.functionName = functionName;
		this.propertyName = propertyName;
	}

	public static ValueLocation makeProperty(String functionName, String propertyName) {
		return new ValueLocation(property, functionName, propertyName);
	}

	public static ValueLocation makeListPropertyElement(String functionName, String propertyName) {
		return new ValueLocation(SourceType.list_property_element, functionName, propertyName);
	}

	public static ValueLocation makeListArgumentElement(String functionName) {
		return new ValueLocation(SourceType.list_argument_element, functionName, null);
	}

	public static ValueLocation makeToplevel() {
		return new ValueLocation(SourceType.toplevel, null, null);
	}

	public static ValueLocation makeArgument(String functionName, String argumentName) {
		return new ValueLocation(SourceType.argument, functionName, argumentName);
	}

	public ValueLocation toListElement() {
		switch (sourceType) {
			case property:
				return makeListPropertyElement(functionName, propertyName);
			case argument:
				return makeListArgumentElement(functionName);
			default:
			return makeToplevel();
		}
	}

	public String describeAsLocation() {
		switch (sourceType) {
			case property:
			case list_property_element:
				return functionName + "." + propertyName;
			default:
				return null;
		}
	}

	public String describeAsValue() {
		switch (sourceType) {
			case property:
				return "value for property \"" + propertyName + "\"";
			case list_property_element:
				return "array element for property \"" + propertyName + "\"";
			case list_argument_element:
				return "array element";
			case argument:
				return "argument";
			default:
				return "object";
		}
	}

	public SourceType getSourceType() {
		return sourceType;
	}

	public String getFunctionName() {
		return functionName;
	}

	public String getPropertyName() {
		return propertyName;
	}
	
}

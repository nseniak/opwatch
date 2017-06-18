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

public class ConstructionContext {

	/**
	 * Name of the processor
	 */
	private String name;
	/**
	 * Script stack captured when the processor was built
	 */
	private ScriptStack stack;

	public ConstructionContext(String name, ScriptStack stack) {
		this.name = name;
		this.stack = stack;
	}

	public String descriptor() {
		StringBuilder builder = new StringBuilder();
		builder.append(getName());
		if (!stack.empty()) {
			builder.append(" built ").append(stack.asString());
		}
		return builder.toString();
	}

	public String getName() {
		return name;
	}

}

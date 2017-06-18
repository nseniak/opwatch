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

import jdk.nashorn.api.scripting.AbstractJSObject;
import org.opwatch.service.ScriptService;

public abstract class PayloadScriptValue extends AbstractJSObject {

	public Object toJavascript(ScriptService scriptService) {
		return this;
	}

	public static String javascriptClassName(Class<?> clazz) {
		return clazz.getSimpleName();
	}

	@Override
	public String getClassName() {
		return javascriptClassName(this.getClass());
	}

	@Override
	public Object getDefaultValue(Class<?> hint) {
		if (hint == String.class) {
			return "[object " + getClassName() + "]";
		} else {
			throw new UnsupportedOperationException("cannot.get.default.number");
		}
	}

}

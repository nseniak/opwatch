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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public abstract class PayloadPojoValue extends PayloadScriptValue {

	@Override
	public Object getSlot(int index) {
		return ScriptRuntime.UNDEFINED;
	}

	@Override
	public Object getMember(String name) {
		Objects.requireNonNull(name);
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
			for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {
				if (desc.getName().equals(name)) {
					return desc.getReadMethod().invoke(this);
				}
			}
		} catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
			// Nothing to do
		}
		return ScriptRuntime.UNDEFINED;
	}

	private static Set<String> excludedProperties
			= new HashSet<>(Arrays.asList("class", "className", "array", "function", "slot", "strictFunction"));

	@Override
	public Set<String> keySet() {
		Set<String> properties = new LinkedHashSet<>();
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(this.getClass());
			for (PropertyDescriptor desc : beanInfo.getPropertyDescriptors()) {
				String name = desc.getName();
				if (!excludedProperties .contains(name)) {
					properties.add(desc.getName());
				}
			}
		} catch (IntrospectionException e) {
			// Nothing to do
		}
		return properties;
	}

}

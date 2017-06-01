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

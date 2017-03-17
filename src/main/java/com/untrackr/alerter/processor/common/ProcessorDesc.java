package com.untrackr.alerter.processor.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class ProcessorDesc {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * For scripting purposes
	 */
	public List<String> properties() {
		List<String> result = new ArrayList<>();
		try {
			BeanInfo info = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] props = info.getPropertyDescriptors();
			for (PropertyDescriptor pd : props) {
				result.add(pd.getName());
			}
		} catch (IntrospectionException e) {
			// Do nothing
		}
		return result;
	}

}

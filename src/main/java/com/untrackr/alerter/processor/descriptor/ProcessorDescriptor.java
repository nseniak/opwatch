package com.untrackr.alerter.processor.descriptor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public abstract class ProcessorDescriptor {

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

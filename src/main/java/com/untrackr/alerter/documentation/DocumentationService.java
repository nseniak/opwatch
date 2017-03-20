package com.untrackr.alerter.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.descriptor.ProcessorDescriptor;
import com.untrackr.alerter.processor.common.ProcessorFactory;
import com.untrackr.alerter.processor.descriptor.StringValue;
import com.untrackr.alerter.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentationService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentationService.class);

	private ObjectMapper objectMapper = new ObjectMapper();

	public <D extends ProcessorDescriptor, P extends Processor> ProcessorDoc documentation(ProcessorFactory<D, P> factory) {
		Class<D> descClass = factory.descriptorClass();
		try {
			BeanInfo info = Introspector.getBeanInfo(descClass);
			PropertyDescriptor[] props = info.getPropertyDescriptors();
			List<ProcessorDescFieldDoc> fieldDocList = new ArrayList<>();
			for (PropertyDescriptor pd : props) {
				String name = pd.getName();
				Type type = pd.getReadMethod().getGenericReturnType();
				String typeName = typeName(type);
				fieldDocList.add(new ProcessorDescFieldDoc(name, typeName, null, "doc"));
			}
			return new ProcessorDoc(factory.name(), fieldDocList, "processor doc");
		} catch (IntrospectionException e) {
			logger.error("Exception while generation documentation: " + descClass.getName(), e);
			return null;
		}
	}

	public String typeName(Type type) {
		if (type instanceof Class) {
			return simpleClassName((Class) type);
		} else if (type instanceof ParameterizedType) {
			return parameterizedTypeName((ParameterizedType) type);
		} else {
			return type.toString();
		}
	}

	public String parameterizedTypeName(ParameterizedType type) {
		Type listType = parameterizedListType(type);
		if (listType != null) {
			return typeName(listType) + " array";
		} else {
			return typeName(type.getRawType());
		}
	}

	public String simpleClassName(Class<?> clazz) {
		if (String.class.isAssignableFrom(clazz)) {
			return "a string";
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return "an integer";
		} else if (Number.class.isAssignableFrom(clazz)) {
			return "a number";
		} else if (ScriptService.JavascriptFunction.class.isAssignableFrom(clazz)) {
			return "a function";
		} else if (ProcessorDescriptor.class.isAssignableFrom(clazz)) {
			return "a processor descriptor";
		} else if (StringValue.class.isAssignableFrom(clazz)) {
			return "a string or function";
		} else if (Processor.class.isAssignableFrom(clazz)) {
			return "a processor";
		} else {
			return "an Object";
		}
	}

	public Type parameterizedListType(ParameterizedType type) {
		ParameterizedType paramType = (ParameterizedType) type;
		Type[] args = paramType.getActualTypeArguments();
		if ((paramType.getRawType() == List.class) && (args.length == 1)) {
			return args[0];
		} else {
			return null;
		}
	}

}

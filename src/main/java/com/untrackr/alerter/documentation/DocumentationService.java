package com.untrackr.alerter.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.config.ConstantOrFilter;
import com.untrackr.alerter.processor.config.JavascriptFunction;
import com.untrackr.alerter.processor.config.ProcessorConfig;
import jdk.nashorn.internal.objects.NativeRegExp;
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

	public <C extends ProcessorConfig, P extends Processor> ProcessorDoc documentation(ProcessorFactory<C, P> factory) {
		Class<C> descClass = factory.configurationClass();
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
			throw new RuntimeError("error while generation documentation: " + descClass.getName() + ": " + e.getMessage(),
					new GlobalExecutionScope(),
					e);
		}
	}

	public String processorCategoryName(ProcessorFactory<?, ?> factory) {
		ProcessorSignature signature = factory.staticSignature();
		if (signature == null) {
			return "special";
		}
		ProcessorSignature.PipeRequirement inputRequirement = signature.getInputRequirement();
		ProcessorSignature.PipeRequirement outputRequirement = signature.getOutputRequirement();
		switch (inputRequirement) {
			case NoData:
				switch (outputRequirement) {
					case Data:
						return "producer";
				}
				break;
			case Data:
				switch (outputRequirement) {
					case NoData:
						return "consumer";
					case Data:
						return "filter";
					case Any:
						return "filter with ignorable output";
				}
				break;
			case Any:
				switch (outputRequirement) {
					case Any:
						return "wildcard";
				}
		}
		return signatureDescriptor(signature);
	}

	public String signatureDescriptor(ProcessorSignature signature) {
		ProcessorSignature.PipeRequirement inputRequirement = signature.getInputRequirement();
		ProcessorSignature.PipeRequirement outputRequirement = signature.getOutputRequirement();
		StringBuilder builder = new StringBuilder();
		builder.append("<input from processor: ");
		builder.append(requirementName(inputRequirement));
		builder.append("; output to processor: ");
		builder.append(requirementName(outputRequirement));
		builder.append(">");
		return builder.toString();
	}

	private String requirementName(ProcessorSignature.PipeRequirement requirement) {
		switch (requirement) {
			case None:
				return "undefined";
			case NoData:
				return "forbidden";
			case Data:
				return "required";
			case Any:
				return "optional";
		}
		throw new IllegalStateException("unknown pipe requirement: " + requirement.name());
	}

	public String typeName(Type type) {
		if (type instanceof Class) {
			return simpleClassName((Class) type);
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			Type listType = parameterizedTypeParameter(paramType, List.class);
			if (listType != null) {
				return listTypeName(listType);
			}
			Type valueType = parameterizedTypeParameter(paramType, ConstantOrFilter.class);
			if (valueType != null) {
				return constantOrFilterTypeName(valueType);
			}
			return typeName(paramType.getRawType());
		}
		return type.toString();
	}

	private String listTypeName(Type listType) {
		return typeName(listType) + " array";
	}

	private String constantOrFilterTypeName(Type valueType) {
		String name = typeName(valueType);
		return name + " or function returning " + name;
	}

	public String simpleClassName(Class<?> clazz) {
		if (String.class.isAssignableFrom(clazz)) {
			return "a string";
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return "an integer";
		} else if (Boolean.class.isAssignableFrom(clazz)) {
			return "a boolean";
		} else if (Number.class.isAssignableFrom(clazz)) {
			return "a number";
		} else if (JavascriptFunction.class.isAssignableFrom(clazz)) {
			return "a function";
		} else if (NativeRegExp.class.isAssignableFrom(clazz)) {
			return "a RegExp";
		} else if (ProcessorConfig.class.isAssignableFrom(clazz)) {
			return "a processor configuration";
		} else if (ConstantOrFilter.class.isAssignableFrom(clazz)) {
			return "a string or function";
		} else if (com.untrackr.alerter.processor.config.JavascriptFunction.class.isAssignableFrom(clazz)) {
			return "a function";
		} else if (Processor.class.isAssignableFrom(clazz)) {
			return "a processor";
		} else {
			return "an Object";
		}
	}

	public Type parameterizedTypeParameter(ParameterizedType paramType, Class<?> clazz) {
		Type[] args = paramType.getActualTypeArguments();
		if ((paramType.getRawType() instanceof Class) && (clazz.isAssignableFrom((Class) paramType.getRawType())) && (args.length == 1)) {
			return args[0];
		} else {
			return null;
		}
	}

}

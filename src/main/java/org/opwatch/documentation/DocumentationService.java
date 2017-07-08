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

package org.opwatch.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.nashorn.internal.objects.NativeRegExp;
import org.javatuples.Pair;
import org.opwatch.processor.common.*;
import org.opwatch.processor.config.Duration;
import org.opwatch.processor.config.JavascriptFunction;
import org.opwatch.processor.config.ProcessorConfig;
import org.opwatch.processor.config.ValueOrFilter;
import org.opwatch.processor.payload.PayloadScriptValue;
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
			return "control";
		}
		ProcessorSignature.DataRequirement inputRequirement = signature.getInputRequirement();
		ProcessorSignature.DataRequirement outputRequirement = signature.getOutputRequirement();
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
					case Data:
						return "producer or filter, depending on the configuration";
				}
		}
		return signatureDescriptor(signature);
	}

	public String signatureDescriptor(ProcessorSignature signature) {
		ProcessorSignature.DataRequirement inputRequirement = signature.getInputRequirement();
		ProcessorSignature.DataRequirement outputRequirement = signature.getOutputRequirement();
		StringBuilder builder = new StringBuilder();
		builder.append("<input from processor: ");
		builder.append(requirementName(inputRequirement));
		builder.append("; output to processor: ");
		builder.append(requirementName(outputRequirement));
		builder.append(">");
		return builder.toString();
	}

	private String requirementName(ProcessorSignature.DataRequirement requirement) {
		switch (requirement) {
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
			Type listType = singleTypeParameter(paramType, List.class);
			if (listType != null) {
				return listTypeName(listType);
			}
			Type valueType = singleTypeParameter(paramType, ValueOrFilter.class);
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
		} else if (JavascriptFunction.class.isAssignableFrom(clazz)) {
			return "a function";
		} else if (Processor.class.isAssignableFrom(clazz)) {
			return "a processor";
		} else if (Duration.class.isAssignableFrom(clazz)) {
			return "a number of milliseconds or duration string";
		} else if (PayloadScriptValue.class.isAssignableFrom(clazz)) {
			return PayloadScriptValue.javascriptClassName(clazz);
		} else {
			return "an Object";
		}
	}

	public Type singleTypeParameter(ParameterizedType paramType, Class<?> clazz) {
		Type[] args = paramType.getActualTypeArguments();
		if ((paramType.getRawType() instanceof Class) && (clazz.isAssignableFrom((Class) paramType.getRawType())) && (args.length == 1)) {
			return args[0];
		} else {
			return null;
		}
	}

	public Pair<Type, Type> pairTypeParameter(ParameterizedType paramType, Class<?> clazz) {
		Type[] args = paramType.getActualTypeArguments();
		if ((paramType.getRawType() instanceof Class) && (clazz.isAssignableFrom((Class) paramType.getRawType())) && (args.length == 2)) {
			return new Pair<>(args[0], args[1]);
		} else {
			return null;
		}
	}

}

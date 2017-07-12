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

import org.opwatch.documentation.ProcessorCategory;
import org.opwatch.processor.config.*;
import org.opwatch.service.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class ProcessorFactory<C extends ProcessorConfig, P extends Processor> {

	private static final Logger logger = LoggerFactory.getLogger(ProcessorFactory.class);

	private String id;

	protected ProcessorService processorService;

	public ProcessorFactory(ProcessorService processorService) {
		this.id = processorService.uuid();
		this.processorService = processorService;
	}

	public abstract String name();

	public abstract Class<C> configurationClass();

	public abstract Class<P> processorClass();

	public ProcessorSignature staticSignature() {
		return null;
	}

	public abstract ProcessorCategory processorCategory();

	public abstract P make(Object scriptObject);

	public ProcessorSchema schema() {
		try {
			ProcessorConfig instance = configurationClass().newInstance();
			List<ConfigPropertySchema> properties = new ArrayList<>();
			for (PropertyDescriptor pd : Introspector.getBeanInfo(configurationClass()).getPropertyDescriptors()) {
				String name = pd.getName();
				if (!name.equals("class")) {
					ConfigPropertySchema schema = new ConfigPropertySchema();
					schema.setName(name);
					Method readMethod = pd.getReadMethod();
					schema.setType(processorService.getScriptService().typeName(readMethod.getGenericReturnType()));
					schema.setImplicit(readMethod.getAnnotation(ImplicitProperty.class) != null);
					boolean optional = readMethod.getAnnotation(OptionalProperty.class) != null;
					if (optional) {
						schema.setOptional(true);
						schema.setDefaultValue(readMethod.invoke(instance));
					}
					properties.add(schema);
				}
			}
			ProcessorSchema config = new ProcessorSchema();
			config.setName(name());
			config.setProperties(properties);
			config.setCategory(processorService.getScriptService().processorCategoryDescription(this));
			return config;
		} catch (InvocationTargetException | InstantiationException | IllegalAccessException | IntrospectionException e) {
			logger.error("Exception while fetching properties: " + configurationClass(), e);
			throw new RuntimeError("cannot fetch properties", new FactoryExecutionScope(this), e);
		}
	}

	public void error(String message) {
		throw new RuntimeError(message, new FactoryExecutionScope(this));
	}

	protected C convertProcessorConfig(Object scriptObject) {
		return (C) processorService.getScriptService().convertScriptValue(ValueLocation.makeArgument(name(), "configuration"), configurationClass(), scriptObject,
				(message) -> new RuntimeError(message, new FactoryExecutionScope(this)));
	}

	public <T> T checkPropertyValue(String property, T value) {
		if (value != null) {
			return value;
		} else {
			ValueLocation location = ValueLocation.makeProperty(name(), property);
			throw new RuntimeError("missing " + location.describeAsValue(), new FactoryExecutionScope(this));
		}
	}

	protected <T> T optionalPropertyValue(String property, T value, T defaultValue) {
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public String getId() {
		return id;
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public void setProcessorService(ProcessorService processorService) {
		this.processorService = processorService;
	}

}

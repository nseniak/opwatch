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

package org.opwatch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Primitives;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.JSType;
import org.javatuples.Pair;
import org.opwatch.documentation.DocumentationService;
import org.opwatch.processor.common.*;
import org.opwatch.processor.config.*;
import org.opwatch.processor.payload.Stats;
import org.opwatch.processor.primitives.consumer.alert.AlertGeneratorFactory;
import org.opwatch.processor.primitives.consumer.log.LogFactory;
import org.opwatch.processor.primitives.consumer.send.SendFactory;
import org.opwatch.processor.primitives.consumer.stdout.StdoutFactory;
import org.opwatch.processor.primitives.control.alias.AliasFactory;
import org.opwatch.processor.primitives.control.parallel.ParallelFactory;
import org.opwatch.processor.primitives.control.pipe.PipeFactory;
import org.opwatch.processor.primitives.filter.apply.ApplyFactory;
import org.opwatch.processor.primitives.filter.collect.CollectFactory;
import org.opwatch.processor.primitives.filter.grep.GrepFactory;
import org.opwatch.processor.primitives.filter.json.JsonFactory;
import org.opwatch.processor.primitives.filter.jstack.JstackFactory;
import org.opwatch.processor.primitives.filter.sh_f.ShFilterFactory;
import org.opwatch.processor.primitives.filter.trail.TrailFactory;
import org.opwatch.processor.primitives.producer.call.CallFactory;
import org.opwatch.processor.primitives.producer.console.StdinFactory;
import org.opwatch.processor.primitives.producer.curl.CurlFactory;
import org.opwatch.processor.primitives.producer.df.DfFactory;
import org.opwatch.processor.primitives.producer.receive.ReceiveFactory;
import org.opwatch.processor.primitives.producer.sh.ShFactory;
import org.opwatch.processor.primitives.producer.stat.StatFactory;
import org.opwatch.processor.primitives.producer.tail.TailFactory;
import org.opwatch.processor.primitives.producer.top.TopFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScriptService {

	private static final Logger logger = LoggerFactory.getLogger(ScriptService.class);

	@Autowired
	private ProcessorService processorService;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private DocumentationService documentationService;

	private static String homeDirectory = System.getProperty("app.home");

	public static final String INIT_SCRIPT_PATH = "_init_scripts/";

	private Map<Class<? extends Processor>, ProcessorFactory<?, ? extends Processor>> factories = new LinkedHashMap<>();

	private NashornScriptEngine scriptEngine;

	private ObjectMapper objectMapper = new ObjectMapper();

	public void initialize() {
		NashornScriptEngineFactory scriptEngineFactory = new NashornScriptEngineFactory();
		scriptEngine = (NashornScriptEngine) scriptEngineFactory.getScriptEngine("-scripting");
		try {
			loadScriptResources();
			createSimplePrimitiveFunction("__factories", this::factories);
			createSimplePrimitiveFunction("__stats", Stats::makeStats);
			createSimpleBinding("config", processorService.config());
			createSimpleBinding("__service", processorService);
			createSimpleFactoryFunction(new AlertGeneratorFactory(processorService));
			createSimpleFactoryFunction(new AliasFactory(processorService));
			createSimpleFactoryFunction(new ApplyFactory(processorService));
			createSimpleFactoryFunction(new CollectFactory(processorService));
			createSimpleFactoryFunction(new ShFactory(processorService));
			createSimpleFactoryFunction(new CurlFactory(processorService));
			createSimpleFactoryFunction(new DfFactory(processorService));
			createSimpleFactoryFunction(new GrepFactory(processorService));
			createSimpleFactoryFunction(new JsonFactory(processorService));
			createSimpleFactoryFunction(new JstackFactory(processorService));
			createSimpleFactoryFunction(new CallFactory(processorService));
			createSimpleFactoryFunction(new LogFactory(processorService));
			createSimpleFactoryFunction(new SendFactory(processorService));
			createSimpleFactoryFunction(new ShFilterFactory(processorService));
			createSimpleFactoryFunction(new StatFactory(processorService));
			createSimpleFactoryFunction(new StdinFactory(processorService));
			createSimpleFactoryFunction(new StdoutFactory(processorService));
			createSimpleFactoryFunction(new TailFactory(processorService));
			createSimpleFactoryFunction(new TopFactory(processorService));
			createSimpleFactoryFunction(new TrailFactory(processorService));
			createVarargFactoryFunction(new ParallelFactory(processorService));
			createVarargFactoryFunction(new PipeFactory(processorService));
			createSimpleFactoryFunction(new ReceiveFactory(processorService));
			loadConfigFile();
		} catch (ScriptException e) {
			throw new RuntimeError("initialization error: " + e.getMessage(), e);
		}
	}

	private void loadScriptResources() {
		Resource[] scriptResources;
		try {
			scriptResources = applicationContext.getResources("classpath:/" + INIT_SCRIPT_PATH + "*.js");
		} catch (IOException e) {
			throw new IllegalStateException("Cannot find script resource: " + e.getMessage(), e);
		}
		for (Resource scriptResource : scriptResources) {
			loadScriptResource(scriptResource);
		}
	}

	public void loadScriptResource(Resource scriptResource) {
		loadScript(() -> new InputStreamReader(scriptResource.getInputStream()), scriptResource.toString());
	}

	private void loadConfigFile() throws ScriptException {
		if (processorService.config().noInit()) {
			return;
		}
		String initFile = new File(new File(homeDirectory), "config.js").getAbsolutePath();
		if (processorService.config().initFile() != null) {
			initFile = processorService.config().initFile();
		}
		loadScript(initFile);
	}

	public void loadScript(String fileOrUrl) {
		loadScript(() -> {
			String scheme = null;
			try {
				scheme = new URI(fileOrUrl).getScheme();
			} catch (URISyntaxException e) {
				throw new RuntimeError("malformed file or url: " + e.getMessage());
			}
			if ((scheme != null) && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
				try {
					return new InputStreamReader(new URL(fileOrUrl).openStream());
				} catch (MalformedURLException e) {
					throw new RuntimeError("malformed url " + fileOrUrl + ": " + e.getMessage());
				} catch (IOException e) {
					throw new RuntimeError("cannot open " + fileOrUrl);
				}
			} else {
				try {
					return new FileReader(new File(fileOrUrl));
				} catch (FileNotFoundException e) {
					throw new RuntimeError("file not found: " + fileOrUrl);
				}
			}
		}, fileOrUrl);
	}

	private void loadScript(ReaderSupplier readerSupplier, String location) {
		ScriptContext context = scriptEngine.getContext();
		context.setAttribute(ScriptEngine.FILENAME, location, ScriptContext.ENGINE_SCOPE);
		try {
			scriptEngine.eval(readerSupplier.reader(), context);
		} catch (FileNotFoundException e) {
			throw new RuntimeError("file not found: " + location);
		} catch (IOException | ScriptException e) {
			throw new RuntimeError(e);
		} finally {
			context.removeAttribute(ScriptEngine.FILENAME, ScriptContext.ENGINE_SCOPE);
		}
	}

	private interface ReaderSupplier {

		Reader reader() throws IOException;

	}

	public void runExpression(String expression) {
		ScriptContext context = scriptEngine.getContext();
		try {
			Object scriptObject = scriptEngine.eval(expression, context);
			Processor processor = (Processor) convertScriptValue(ValueLocation.makeToplevel(), Processor.class, scriptObject,
					RuntimeError::new);
			processor.run();
		} catch (ScriptException e) {
			throw new RuntimeError(e);
		}
	}

	public String pretty(Object object) {
		synchronized (this) {
			try {
				return (String) scriptEngine.invokeFunction("pretty", object);
			} catch (NoSuchMethodException | ScriptException e) {
				throw new RuntimeError(e);
			}
		}
	}

	public String jsonStringify(Object object) {
		synchronized (this) {
			try {
				return (String) scriptEngine.invokeFunction("__json_stringify", object);
			} catch (NoSuchMethodException | ScriptException e) {
				throw new RuntimeError(e);
			}
		}
	}

	public Object jsonParse(String string) {
		synchronized (this) {
			try {
				return scriptEngine.invokeFunction("__json_parse", string);
			} catch (NoSuchMethodException | ScriptException e) {
				throw new RuntimeError(e);
			}
		}
	}

	public Object array(Object[] objects) {
		synchronized (this) {
			try {
				return scriptEngine.invokeFunction("Array", objects);
			} catch (NoSuchMethodException | ScriptException e) {
				throw new RuntimeError(e);
			}
		}
	}

	private <C extends ProcessorConfig, T extends Processor> void createSimpleFactoryFunction(ProcessorFactory<C, T> processorFactory) throws ScriptException {
		createFactoryFunction(processorFactory, "factory_wrapper");
	}

	private <C extends ProcessorConfig, T extends Processor> void createVarargFactoryFunction(ProcessorFactory<C, T> processorFactory) throws ScriptException {
		createFactoryFunction(processorFactory, "vararg_factory_wrapper");
	}

	private <C extends ProcessorConfig, T extends Processor> void createFactoryFunction(ProcessorFactory<C, T> processorFactory, String wrapperName) throws ScriptException {
		factories.put(processorFactory.processorClass(), processorFactory);
		ScriptContext context = scriptEngine.getContext();
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		String name = processorFactory.name();
		String factoryName = "__" + name + "_factory";
		bindings.put(factoryName, processorFactory);
		scriptEngine.eval(String.format("%1$s = %2$s(%3$s)", name, wrapperName, factoryName));
	}

	private void createSimpleBinding(String name, Object object) {
		ScriptContext context = scriptEngine.getContext();
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put(name, object);
	}

	private void createSimplePrimitiveFunction(String name, JavascriptFunction0 function) throws ScriptException {
		createSimpleBinding(name, function);
	}

	private void createSimplePrimitiveFunction(String name, JavascriptFunction1 function) throws ScriptException {
		createSimpleBinding(name, function);
	}

	private void createSimplePrimitiveFunction(String name, JavascriptFunction2 function) throws ScriptException {
		createSimpleBinding(name, function);
	}

	private List<ProcessorFactory<?, ?>> factories() {
		return new ArrayList<>(factories.values());
	}

	public ProcessorFactory<?, ?> factory(Class<?> processorClass) {
		return factories.get(processorClass);
	}

	public void executeConsoleInput(String script) {
		ScriptContext context = scriptEngine.getContext();
		processorService.withExceptionHandling(null,
				GlobalExecutionScope::new,
				() -> {
					Object value = scriptEngine.eval(script, context);
					if (value != null) {
						processorService.printStdout(JSType.toString(value));
					}
				});
	}

	@FunctionalInterface
	public interface JavascriptFunction0<R> {

		R apply();

	}

	@FunctionalInterface
	public interface JavascriptFunction1<T, R> {

		R apply(T arg);

	}

	@FunctionalInterface
	public interface JavascriptFunction2<T1, T2, R> {

		R apply(T1 arg1, T2 arg2);

	}

	public Object convertScriptValue(ValueLocation valueLocation, Type type, Object scriptValue, RuntimeExceptionFactory exceptionFactory) {
		if (scriptValue == null) {
			return null;
		}
		if (type instanceof Class) {
			return convertScriptValueToClass(valueLocation, (Class) type, scriptValue, exceptionFactory);
		}
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			Type listElementType = documentationService.singleTypeParameter(paramType, List.class);
			if (listElementType != null) {
				return convertList(valueLocation, type, listElementType, scriptValue, exceptionFactory);
			}
			Type valueOrFilterValueType = documentationService.singleTypeParameter(paramType, ValueOrFilter.class);
			if (valueOrFilterValueType != null) {
				return convertValueOrFilter(valueLocation, type, valueOrFilterValueType, scriptValue, exceptionFactory);
			}
			Type valueOrListValueType = documentationService.singleTypeParameter(paramType, ValueOrList.class);
			if (valueOrListValueType != null) {
				return convertValueOrList(valueLocation, type, valueOrListValueType, scriptValue, exceptionFactory);
			}
			Pair<Type, Type> mapTypes = documentationService.pairTypeParameter(paramType, Map.class);
			if ((mapTypes != null) && (mapTypes.getValue0() == String.class)) {
				return convertStringMap(valueLocation, type, mapTypes.getValue1(), scriptValue, exceptionFactory);
			}
			return convertScriptValue(valueLocation, paramType.getRawType(), scriptValue, exceptionFactory);
		}
		return throwError(valueLocation, type, scriptValue, exceptionFactory);
	}

	private Void throwError(ValueLocation valueLocation, Type type, Object scriptValue, RuntimeExceptionFactory exceptionFactory) {
		throw exceptionFactory.make("invalid " + valueLocation.describeAsValue() + ", expected " + documentationService.typeName(type) + ", got: " + scriptValue);
	}

	private List<Object> convertList(ValueLocation valueLocation, Type listType, Type elementType, Object scriptValue, RuntimeExceptionFactory exceptionFactory) {
		List<Object> scriptList = (List<Object>) ScriptUtils.convert(scriptValue, List.class);
		List<Object> list = new ArrayList<>();
		for (Object scriptListObject : scriptList) {
			list.add(convertScriptValue(valueLocation.toListElement(), elementType, scriptListObject, exceptionFactory));
		}
		return list;
	}

	private Object convertValueOrFilter(ValueLocation valueLocation, Type type, Type valueType, Object scriptValue, RuntimeExceptionFactory exceptionFactory) {
		if ((scriptValue instanceof ScriptObjectMirror) && ((ScriptObjectMirror) scriptValue).isFunction()) {
			return ValueOrFilter.makeFunction(new JavascriptFilter((ScriptObjectMirror) scriptValue, valueLocation, processorService));
		}
		return ValueOrFilter.makeValue(convertScriptValue(valueLocation, valueType, scriptValue, exceptionFactory));
	}

	private <T> Object convertValueOrList(ValueLocation valueLocation, Type type, Type valueType, Object scriptValue, RuntimeExceptionFactory exceptionFactory) {
		if ((scriptValue instanceof ScriptObjectMirror) && ((ScriptObjectMirror) scriptValue).isArray()) {
			List<T> list = new ArrayList<>();
			for (Object element : convertList(valueLocation, type, valueType, scriptValue, exceptionFactory)) {
				list.add((T) element);
			}
			return ValueOrList.makeList(list);
		}
		return ValueOrList.makeValue(convertScriptValue(valueLocation, valueType, scriptValue, exceptionFactory));
	}

	private <T> Map<String, T> convertStringMap(ValueLocation valueLocation, Type type, Type valueType, Object scriptValue, RuntimeExceptionFactory exceptionFactory) {
		if (!(scriptValue instanceof ScriptObjectMirror)) {
			throwError(valueLocation, type, scriptValue, exceptionFactory);
		}
		ScriptObjectMirror som = (ScriptObjectMirror) scriptValue;
		Map<String, T> result = new LinkedHashMap<>();
		for (String key : som.getOwnKeys(true)) {
			String processorName = valueLocation.getFunctionName();
			ValueLocation keyValueLocation = ValueLocation.makeProperty(processorName, key);
			T value = (T) convertScriptValue(keyValueLocation, valueType, som.get(key), exceptionFactory);
			result.put(key, value);
		}
		return result;
	}

	private Object convertScriptValueToClass(ValueLocation valueLocation, Class<?> clazz, Object scriptValue, RuntimeExceptionFactory exceptionFactory) {
		if (clazz.isAssignableFrom(scriptValue.getClass())) {
			return scriptValue;
		}
		if (Number.class.isAssignableFrom(clazz)) {
			Object number = convertScriptValueToNumber((Class<? extends Number>) clazz, scriptValue);
			if (number != null) {
				return number;
			}
		}
		if (Duration.class.isAssignableFrom(clazz)) {
			if (scriptValue instanceof String) {
				return Duration.makeText((String) scriptValue);
			}
			Object number = convertScriptValueToNumber(Long.class, scriptValue);
			if (number != null) {
				return Duration.makeMilliseconds((long) number);
			}
		}
		if (scriptValue instanceof ScriptObjectMirror) {
			ScriptObjectMirror scriptObject = (ScriptObjectMirror) scriptValue;
			if (scriptObject.isFunction()) {
				if (clazz == JavascriptFilter.class) {
					return new JavascriptFilter(scriptObject, valueLocation, processorService);
				} else if (clazz == JavascriptPredicate.class) {
					return new JavascriptPredicate(scriptObject, valueLocation, processorService);
				} else if (clazz == JavascriptConsumer.class) {
					return new JavascriptConsumer(scriptObject, valueLocation, processorService);
				} else if (clazz == JavascriptProducer.class) {
					return new JavascriptProducer(scriptObject, valueLocation, processorService);
				}
			} else {
				Object sobj = ScriptObjectMirror.unwrap(scriptValue, Context.getGlobal());
				if (clazz.isAssignableFrom(sobj.getClass())) {
					return sobj;
				}
				return convertBinding(valueLocation, clazz, scriptObject, scriptObject.getOwnKeys(true), exceptionFactory);
			}
		}
		if (scriptValue instanceof Map) {
			Map mapObject = (Map) scriptValue;
			String[] properties = new String[mapObject.size()];
			mapObject.keySet().toArray(properties);
			return convertBinding(valueLocation, clazz, mapObject, properties, exceptionFactory);
		}
		return throwError(valueLocation, clazz, scriptValue, exceptionFactory);
	}

	private Object convertBinding(ValueLocation valueLocation, Class<?> clazz, Map scriptValue, String[] properties, RuntimeExceptionFactory exceptionFactory) {
		if (clazz.isAssignableFrom(scriptValue.getClass())) {
			return scriptValue;
		}
		try {
			Object value = BeanUtils.instantiate(clazz);
			BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
			for (String propertyName : properties) {
				try {
					PropertyDescriptor descriptor = wrapper.getPropertyDescriptor(propertyName);
					Type fieldType = descriptor.getReadMethod().getGenericReturnType();
					String processorName = valueLocation.getFunctionName();
					ValueLocation propertyValueSource = ValueLocation.makeProperty(processorName, propertyName);
					wrapper.setPropertyValue(propertyName, convertScriptValue(propertyValueSource, fieldType, scriptValue.get(propertyName), exceptionFactory));
				} catch (InvalidPropertyException e) {
					throw exceptionFactory.make("invalid property \"" + propertyName + "\"");
				}
			}
			return value;
		} catch (BeanInstantiationException e) {
			// BeanUtils.instantiate raised an error. The class is either abstract or doesn't have a default constructor.
			// Fall back to error
		}
		return throwError(valueLocation, clazz, scriptValue, exceptionFactory);
	}

	public Object convertScriptValueToNumber(Class<? extends Number> clazz, Object scriptValue) {
		try {
			if (clazz == Integer.class) {
				return ((Number) scriptValue).intValue();
			} else if (clazz == Long.class) {
				return ((Number) scriptValue).longValue();
			} else if (clazz == Float.class) {
				return ((Number) scriptValue).floatValue();
			} else if (clazz == Double.class) {
				return ((Number) scriptValue).doubleValue();
			}
		} catch (ClassCastException e) {
			// Nothing to do
		}
		return null;
	}

	public boolean bean(Object object) {
		return (object != null) && !(object instanceof String) && !Primitives.isWrapperType(object.getClass());
	}

	public void mapFields(Object object, fieldHandler handler) {
		if (object instanceof ScriptObjectMirror) {
			ScriptObjectMirror mirror = (ScriptObjectMirror) object;
			for (String key : mirror.getOwnKeys(true)) {
				handler.handle(key, mirror.get(key));
			}
		} else {
			try {
				for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(object.getClass()).getPropertyDescriptors()) {
					String key = propertyDescriptor.getName();
					if ((!key.equals("class") && (propertyDescriptor.getReadMethod() != null))) {
						Object value = propertyDescriptor.getReadMethod().invoke(object);
						handler.handle(key, value);
					}
				}
			} catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
				logger.error("Cannot map fields: " + object.getClass().getName(), e);
			}
		}
	}

	public interface fieldHandler {

		void handle(String key, Object value);

	}

	public String typeName(Type type) {
		return documentationService.typeName(type);
	}

	public String processorCategoryName(ProcessorFactory<?, ?> factory) {
		return documentationService.processorCategoryName(factory);
	}

	/**
	 * Exported for scripting purposes
	 */
	public static void logInfo(String message) {
		logger.info(message);
	}

	/**
	 * Exported for scripting purposes
	 */
	public Object eval(String str, String location) throws ScriptException {
		ScriptContext context = scriptEngine.getContext();
		context.setAttribute(ScriptEngine.FILENAME, location, ScriptContext.ENGINE_SCOPE);
		return scriptEngine.eval(str, context);
	}

	public NashornScriptEngine getScriptEngine() {
		return scriptEngine;
	}

	public static void setHomeDirectory(String homeDirectory) {
		ScriptService.homeDirectory = homeDirectory;
	}

	public static String getHomeDirectory() {
		return homeDirectory;
	}

}

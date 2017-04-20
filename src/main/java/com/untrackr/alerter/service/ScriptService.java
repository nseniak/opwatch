package com.untrackr.alerter.service;

import com.google.common.primitives.Primitives;
import com.untrackr.alerter.documentation.DocumentationService;
import com.untrackr.alerter.documentation.ProcessorDoc;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.config.*;
import com.untrackr.alerter.processor.payload.Stats;
import com.untrackr.alerter.processor.primitives.consumer.alert.AlertGeneratorFactory;
import com.untrackr.alerter.processor.primitives.consumer.send.SendFactory;
import com.untrackr.alerter.processor.primitives.consumer.stdout.StdoutFactory;
import com.untrackr.alerter.processor.primitives.control.alias.AliasFactory;
import com.untrackr.alerter.processor.primitives.control.parallel.ParallelFactory;
import com.untrackr.alerter.processor.primitives.control.pipe.PipeFactory;
import com.untrackr.alerter.processor.primitives.filter.apply.ApplyFactory;
import com.untrackr.alerter.processor.primitives.filter.collect.CollectFactory;
import com.untrackr.alerter.processor.primitives.filter.count.CountFactory;
import com.untrackr.alerter.processor.primitives.filter.grep.GrepFactory;
import com.untrackr.alerter.processor.primitives.filter.json.JsonFactory;
import com.untrackr.alerter.processor.primitives.filter.jstack.JstackFactory;
import com.untrackr.alerter.processor.primitives.filter.sh.ShFactory;
import com.untrackr.alerter.processor.primitives.filter.stdout.TraceFactory;
import com.untrackr.alerter.processor.primitives.filter.test.TestFactory;
import com.untrackr.alerter.processor.primitives.producer.console.StdinFactory;
import com.untrackr.alerter.processor.primitives.producer.cron.CronFactory;
import com.untrackr.alerter.processor.primitives.producer.curl.CurlFactory;
import com.untrackr.alerter.processor.primitives.producer.df.DfFactory;
import com.untrackr.alerter.processor.primitives.producer.receive.ReceiveFactory;
import com.untrackr.alerter.processor.primitives.producer.repeat.RepeatFactory;
import com.untrackr.alerter.processor.primitives.producer.stat.StatFactory;
import com.untrackr.alerter.processor.primitives.producer.tail.TailFactory;
import com.untrackr.alerter.processor.primitives.producer.top.TopFactory;
import com.untrackr.alerter.processor.primitives.producer.trail.TrailFactory;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.runtime.Context;
import jdk.nashorn.internal.runtime.JSType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.script.*;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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

	public static final String INIT_SCRIPT_PATH = "_init_scripts/";

	private Map<Class<? extends Processor>, ProcessorFactory<?, ? extends Processor>> factories = new LinkedHashMap<>();

	private static NashornScriptEngine scriptEngine;

	public void initialize() {
		scriptEngine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
		loadScriptResources();
		try {
			createSimplePrimitiveFunction("__factories", this::factories);
			createSimplePrimitiveFunction("__stats", Stats::makeStats);
			createSimpleBinding("config", processorService.config());
			createSimpleBinding("__service", processorService);
			createVarargFactoryFunction(new ParallelFactory(processorService));
			createVarargFactoryFunction(new PipeFactory(processorService));
			createSimpleFactoryFunction(new AliasFactory(processorService));
			createSimpleFactoryFunction(new StdinFactory(processorService));
			createSimpleFactoryFunction(new GrepFactory(processorService));
			createSimpleFactoryFunction(new ApplyFactory(processorService));
			createSimpleFactoryFunction(new TestFactory(processorService));
			createSimpleFactoryFunction(new CollectFactory(processorService));
			createSimpleFactoryFunction(new TailFactory(processorService));
			createSimpleFactoryFunction(new StdoutFactory(processorService));
			createSimpleFactoryFunction(new TraceFactory(processorService));
			createSimpleFactoryFunction(new StatFactory(processorService));
			createSimpleFactoryFunction(new DfFactory(processorService));
			createSimpleFactoryFunction(new TopFactory(processorService));
			createSimpleFactoryFunction(new AlertGeneratorFactory(processorService));
			createSimpleFactoryFunction(new CurlFactory(processorService));
			createSimpleFactoryFunction(new CountFactory(processorService));
			createSimpleFactoryFunction(new TrailFactory(processorService));
			createSimpleFactoryFunction(new ReceiveFactory(processorService));
			createSimpleFactoryFunction(new SendFactory(processorService));
			createSimpleFactoryFunction(new CronFactory(processorService));
			createSimpleFactoryFunction(new RepeatFactory(processorService));
			createSimpleFactoryFunction(new ShFactory(processorService));
			createSimpleFactoryFunction(new JstackFactory(processorService));
			createSimpleFactoryFunction(new JsonFactory(processorService));
			loadInitFile();
			ProcessorDoc doc = documentationService.documentation(new CurlFactory(processorService));
			logger.info("Doc");
		} catch (ScriptException e) {
			throw new RuntimeError("initialization error: " + e.getMessage(), e);
		}
	}

	private void loadScriptResources() {
		// Load NPM first
		loadScriptResource(applicationContext.getResource("classpath:/" + INIT_SCRIPT_PATH + "npm/jvm-npm.js"));
		// Load the other scripts
		Resource[] scriptResources = new Resource[0];
		try {
			scriptResources = applicationContext.getResources("classpath:/" + INIT_SCRIPT_PATH + "startup/*.js");
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

	private void loadInitFile() {
		if (processorService.config().noInit()) {
			return;
		}
		String initFile = new File(new File(System.getProperty("user.dir")), "init.js").getAbsolutePath();
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
			Type elementType = documentationService.parameterizedTypeParameter(paramType, List.class);
			if (elementType != null) {
				try {
					List<Object> scriptList = (List<Object>) ScriptUtils.convert(scriptValue, List.class);
					return convertList(valueLocation, type, elementType, scriptList, exceptionFactory);
				} catch (Throwable t) {
					// Do nothing
				}
			} else {
				Type valueType = documentationService.parameterizedTypeParameter(paramType, ConstantOrFilter.class);
				if (valueType != null) {
					return convertConstantOrFilter(valueLocation, type, valueType, scriptValue, exceptionFactory);
				} else {
					return convertScriptValue(valueLocation, paramType.getRawType(), scriptValue, exceptionFactory);
				}
			}
		}
		throw exceptionFactory.make("invalid " + valueLocation.describeAsValue() + ", expected " + documentationService.typeName(type) + ", got: " + scriptValue);
	}

	private Object convertList(ValueLocation valueLocation, Type listType, Type elementType, List<Object> scriptList, RuntimeExceptionFactory exceptionFactory) {
		List<Object> list = new ArrayList<>();
		for (Object scriptListObject : scriptList) {
			list.add(convertScriptValue(valueLocation.toListElement(), elementType, scriptListObject, exceptionFactory));
		}
		return list;
	}

	private Object convertConstantOrFilter(ValueLocation valueLocation, Type type, Type valueType, Object scriptValue, RuntimeExceptionFactory exceptionFactory) {
		if ((scriptValue instanceof ScriptObjectMirror) && ((ScriptObjectMirror) scriptValue).isFunction()) {
			return ConstantOrFilter.makeFunctional(new JavascriptFilter((ScriptObjectMirror) scriptValue, valueLocation), valueLocation);
		}
		return ConstantOrFilter.makeConstant(convertScriptValue(valueLocation, valueType, scriptValue, exceptionFactory));
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
		if (scriptValue instanceof ScriptObjectMirror) {
			ScriptObjectMirror scriptObject = (ScriptObjectMirror) scriptValue;
			if (scriptObject.isFunction()) {
				if (clazz == JavascriptFilter.class) {
					return new JavascriptFilter(scriptObject, valueLocation);
				} else if (clazz == JavascriptPredicate.class) {
					return new JavascriptPredicate(scriptObject, valueLocation);
				} else if (clazz == JavascriptProducer.class) {
					return new JavascriptProducer(scriptObject, valueLocation);
				}
			} else {
				Object sobj = ScriptObjectMirror.unwrap(scriptValue, Context.getGlobal());
				if (clazz.isAssignableFrom(sobj.getClass())) {
					return sobj;
				}
				if (!Modifier.isAbstract(clazz.getModifiers())) {
					Object value = BeanUtils.instantiate(clazz);
					BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
					for (String propertyName : scriptObject.getOwnKeys(true)) {
						try {
							PropertyDescriptor descriptor = wrapper.getPropertyDescriptor(propertyName);
							Type fieldType = descriptor.getReadMethod().getGenericReturnType();
							String processorName = valueLocation.getFunctionName();
							ValueLocation propertyValueSource = ValueLocation.makeProperty(processorName, propertyName);
							wrapper.setPropertyValue(propertyName, convertScriptValue(propertyValueSource, fieldType, scriptObject.get(propertyName), exceptionFactory));
						} catch (InvalidPropertyException e) {
							throw exceptionFactory.make("invalid property \"" + propertyName + "\"");
						}
					}
					return value;
				}
			}
		}
		throw exceptionFactory.make("invalid " + valueLocation.describeAsValue() + ", expected " + documentationService.typeName(clazz) + ", got: " + scriptValue);
	}

	private Object convertScriptValueToNumber(Class<? extends Number> clazz, Object scriptValue) {
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
					if (!key.equals("class")) {
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
	public static Object eval(String str, String location) throws ScriptException {
		ScriptContext context = scriptEngine.getContext();
		context.setAttribute(ScriptEngine.FILENAME, location, ScriptContext.ENGINE_SCOPE);
		return scriptEngine.eval(str, context);
	}

	public NashornScriptEngine getScriptEngine() {
		return scriptEngine;
	}

}

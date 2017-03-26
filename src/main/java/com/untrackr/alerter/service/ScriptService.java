package com.untrackr.alerter.service;

import com.untrackr.alerter.documentation.DocumentationService;
import com.untrackr.alerter.documentation.ProcessorDoc;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.config.*;
import com.untrackr.alerter.processor.primitives.consumer.alert.AlertGeneratorFactory;
import com.untrackr.alerter.processor.primitives.consumer.post.PostFactory;
import com.untrackr.alerter.processor.primitives.filter.collect.CollectFactory;
import com.untrackr.alerter.processor.primitives.filter.grep.GrepFactory;
import com.untrackr.alerter.processor.primitives.filter.json.JsonFactory;
import com.untrackr.alerter.processor.primitives.filter.jstack.JstackFactory;
import com.untrackr.alerter.processor.primitives.filter.sh.ShFactory;
import com.untrackr.alerter.processor.primitives.filter.stdout.StdoutFactory;
import com.untrackr.alerter.processor.primitives.filter.apply.ApplyFactory;
import com.untrackr.alerter.processor.primitives.producer.console.StdinFactory;
import com.untrackr.alerter.processor.primitives.producer.count.CountFactory;
import com.untrackr.alerter.processor.primitives.producer.cron.CronFactory;
import com.untrackr.alerter.processor.primitives.producer.curl.CurlFactory;
import com.untrackr.alerter.processor.primitives.producer.df.DfFactory;
import com.untrackr.alerter.processor.primitives.producer.receive.ReceiveFactory;
import com.untrackr.alerter.processor.primitives.producer.repeat.RepeatFactory;
import com.untrackr.alerter.processor.primitives.producer.stat.StatFactory;
import com.untrackr.alerter.processor.primitives.producer.tail.TailFactory;
import com.untrackr.alerter.processor.primitives.producer.top.TopFactory;
import com.untrackr.alerter.processor.primitives.producer.trail.TrailFactory;
import com.untrackr.alerter.processor.primitives.special.alias.AliasFactory;
import com.untrackr.alerter.processor.primitives.special.parallel.ParallelFactory;
import com.untrackr.alerter.processor.primitives.special.pipe.PipeFactory;
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
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScriptService {

	private static final Logger logger = LoggerFactory.getLogger(ScriptService.class);

	@Autowired
	private ProcessorService processorService;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private DocumentationService documentationService;

	private static NashornScriptEngine scriptEngine;

	public void initialize() {
		scriptEngine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
		loadScriptResources();
		try {
			createSimplePrimitiveFunction("run", processorService::runProcessor);
			createVarargFactoryFunction(new ParallelFactory(processorService));
			createVarargFactoryFunction(new PipeFactory(processorService));
			createSimpleFactoryFunction(new AliasFactory(processorService));
			createSimpleFactoryFunction(new StdinFactory(processorService));
			createSimpleFactoryFunction(new GrepFactory(processorService));
			createSimpleFactoryFunction(new ApplyFactory(processorService));
			createSimpleFactoryFunction(new CollectFactory(processorService));
			createSimpleFactoryFunction(new TailFactory(processorService));
			createSimpleFactoryFunction(new StdoutFactory(processorService));
			createSimpleFactoryFunction(new StatFactory(processorService));
			createSimpleFactoryFunction(new DfFactory(processorService));
			createSimpleFactoryFunction(new DfFactory(processorService));
			createSimpleFactoryFunction(new TopFactory(processorService));
			createSimpleFactoryFunction(new AlertGeneratorFactory(processorService));
			createSimpleFactoryFunction(new CurlFactory(processorService));
			createSimpleFactoryFunction(new CountFactory(processorService));
			createSimpleFactoryFunction(new TrailFactory(processorService));
			createSimpleFactoryFunction(new ReceiveFactory(processorService));
			createSimpleFactoryFunction(new PostFactory(processorService));
			createSimpleFactoryFunction(new CronFactory(processorService));
			createSimpleFactoryFunction(new RepeatFactory(processorService));
			createSimpleFactoryFunction(new ShFactory(processorService));
			createSimpleFactoryFunction(new JstackFactory(processorService));
			createSimpleFactoryFunction(new JsonFactory(processorService));
			ProcessorDoc doc = documentationService.documentation(new CurlFactory(processorService));
			logger.info("Doc");
		} catch (ScriptException e) {
			throw new AlerterException(e, ExceptionContext.makeToplevel());
		}
	}

	private void loadScriptResources() {
		// Load NPM first
		loadScriptResource(applicationContext.getResource("classpath:/scripts/npm/jvm-npm.js"));
		// Load the other scripts
		Resource[] scriptResources = new Resource[0];
		try {
			scriptResources = applicationContext.getResources("classpath:/scripts/startup/*.js");
		} catch (IOException e) {
			logger.error("Cannot determine javascript startup resources: " + e.getMessage(), e);
		}
		for (Resource scriptResource : scriptResources) {
			loadScriptResource(scriptResource);
		}
	}

	public void loadScriptResource(Resource scriptResource) {
		loadScript(() -> new InputStreamReader(scriptResource.getInputStream()), scriptResource.toString());
	}

	public void loadScriptFile(File file) {
		loadScript(() -> new FileReader(file), file.getPath());
	}

	private void loadScript(ReaderSupplier readerSupplier, String location) {
		processorService.withToplevelErrorHandling(() -> {
			ScriptContext context = scriptEngine.getContext();
			context.setAttribute(ScriptEngine.FILENAME, location, ScriptContext.ENGINE_SCOPE);
			try {
				scriptEngine.eval(readerSupplier.reader(), context);
			} catch (FileNotFoundException e) {
				throw new AlerterException("File not found: " + location, ExceptionContext.makeToplevel());
			} catch (Exception e) {
				throw new AlerterException(e, ExceptionContext.makeToplevel());
			} finally {
				context.removeAttribute(ScriptEngine.FILENAME, ScriptContext.ENGINE_SCOPE);
			}
		});
	}

	private interface ReaderSupplier {

		Reader reader() throws IOException;

	}

	private <D extends ProcessorConfig, T extends Processor> void createSimpleFactoryFunction(ProcessorFactory<D, T> processorFactory) throws ScriptException {
		createFactoryFunction(processorFactory, "factory_wrapper");
	}

	private <D extends ProcessorConfig, T extends Processor> void createVarargFactoryFunction(ProcessorFactory<D, T> processorFactory) throws ScriptException {
		createFactoryFunction(processorFactory, "vararg_factory_wrapper");
	}

	private <D extends ProcessorConfig, T extends Processor> void createFactoryFunction(ProcessorFactory<D, T> processorFactory, String wrapperName) throws ScriptException {
		ScriptContext context = scriptEngine.getContext();
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		String name = processorFactory.name();
		String factoryName = "__" + name + "_factory";
		bindings.put(factoryName, processorFactory);
		scriptEngine.eval(String.format("%1$s = %2$s(%3$s)", name, wrapperName, factoryName));
	}

	private void createSimplePrimitiveFunction(String name, JavascriptFunction function) throws ScriptException {
		ScriptContext context = scriptEngine.getContext();
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put(name, function);
	}

	public void executeConsoleInput(String script) {
		ScriptContext context = scriptEngine.getContext();
		try {
			Object value = scriptEngine.eval(script, context);
			if (value != null) {
				processorService.printMessage(JSType.toString(value));
			}
		} catch (Throwable t) {
			processorService.displayAlerterException(new AlerterException(t, ExceptionContext.makeToplevel()));
		}
	}

	private <T> JavascriptFunction javascriptFunction(JavascriptFunction<Object, T> fun) {
		return fun;
	}

	@FunctionalInterface
	public interface JavascriptFunction<T, R> {
		R apply(T t);
	}

	public Object convertScriptValue(ValueLocation valueLocation, Type type, Object scriptValue, ExceptionContextFactory contextFactory) {
		if (scriptValue == null) {
			return null;
		} else if (type instanceof Class) {
			return convertScriptValueToClass(valueLocation, (Class) type, scriptValue, contextFactory);
		} else if (type instanceof ParameterizedType) {
			return convertScriptValueToParameterizedType(valueLocation, (ParameterizedType) type, scriptValue, contextFactory);
		}
		throw new AlerterException("invalid " + valueLocation.describeAsValue() + ", expected " + documentationService.typeName(type) + ", got: " + scriptValue,
				contextFactory.make());
	}

	private Object convertScriptValueToParameterizedType(ValueLocation valueLocation, ParameterizedType type, Object scriptValue, ExceptionContextFactory contextFactory) {
		Type listType = documentationService.parameterizedListType(type);
		if (listType == null) {
			return convertScriptValue(valueLocation, type.getRawType(), scriptValue, contextFactory);
		} else {
			try {
				ScriptObjectMirror scriptObject = (ScriptObjectMirror) scriptValue;
				List<Object> scriptList = (List<Object>) ScriptUtils.convert(scriptObject, List.class);
				List<Object> list = new ArrayList<>();
				for (Object scriptListObject : scriptList) {
					list.add(convertScriptValue(valueLocation.toListElement(), listType, scriptListObject, contextFactory));
				}
				return list;
			} catch (ClassCastException e) {
				// Nothing to do
			}
		}
		throw new AlerterException("invalid " + valueLocation.describeAsValue() + ", expected " + documentationService.typeName(type) + ", got: " + scriptValue,
				contextFactory.make());
	}


	private Object convertScriptValueToClass(ValueLocation valueLocation, Class<?> clazz, Object scriptValue, ExceptionContextFactory contextFactory) {
		if (clazz.isAssignableFrom(scriptValue.getClass())) {
			return scriptValue;
		}
		if (clazz == StringValue.class) {
			if (String.class.isAssignableFrom(scriptValue.getClass())) {
				return StringValue.makeConstant((String) scriptValue);
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
				} else if (clazz == StringValue.class) {
					return StringValue.makeFunctional(new JavascriptFilter(scriptObject, valueLocation), valueLocation);
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
							wrapper.setPropertyValue(propertyName, convertScriptValue(propertyValueSource, fieldType, scriptObject.get(propertyName), contextFactory));
						} catch (InvalidPropertyException e) {
							throw new AlerterException("invalid property \"" + propertyName + "\"", contextFactory.make());
						}
					}
					return value;
				}
			}
		}
		throw new AlerterException("invalid " + valueLocation.describeAsValue() + ", expected " + documentationService.typeName(clazz) + ", got: " + scriptValue,
				contextFactory.make());
	}

	public interface ExceptionContextFactory {

		ExceptionContext make();

	}

	public String typeName(Type type) {
		return documentationService.typeName(type);
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

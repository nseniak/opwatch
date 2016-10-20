package com.untrackr.alerter.service;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.consumer.alert.AlertGeneratorFactory;
import com.untrackr.alerter.processor.consumer.post.PostFactory;
import com.untrackr.alerter.processor.producer.console.ConsoleFactory;
import com.untrackr.alerter.processor.producer.count.CountFactory;
import com.untrackr.alerter.processor.producer.cron.CronFactory;
import com.untrackr.alerter.processor.producer.curl.CurlFactory;
import com.untrackr.alerter.processor.producer.df.DfFactory;
import com.untrackr.alerter.processor.producer.jscron.cron.JSCronFactory;
import com.untrackr.alerter.processor.producer.receive.ReceiveFactory;
import com.untrackr.alerter.processor.producer.stat.StatFactory;
import com.untrackr.alerter.processor.producer.tail.TailFactory;
import com.untrackr.alerter.processor.producer.top.TopFactory;
import com.untrackr.alerter.processor.producer.trail.TrailFactory;
import com.untrackr.alerter.processor.special.parallel.ParallelFactory;
import com.untrackr.alerter.processor.special.pipe.PipeFactory;
import com.untrackr.alerter.processor.transformer.collect.CollectFactory;
import com.untrackr.alerter.processor.transformer.grep.GrepFactory;
import com.untrackr.alerter.processor.transformer.js.JSFactory;
import com.untrackr.alerter.processor.transformer.jsgrep.JSGrepFactory;
import com.untrackr.alerter.processor.transformer.jstack.JstackFactory;
import com.untrackr.alerter.processor.transformer.once.OnceFactory;
import com.untrackr.alerter.processor.transformer.print.PrintFactory;
import com.untrackr.alerter.processor.transformer.sh.ShFactory;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

	private static NashornScriptEngine scriptEngine;

	public void initialize() {
		scriptEngine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
		loadScriptResources();
		createFactoryFunction(new ParallelFactory(processorService));
		createFactoryFunction(new PipeFactory(processorService));
		createFactoryFunction(new ConsoleFactory(processorService));
		createFactoryFunction(new GrepFactory(processorService));
		createFactoryFunction(new JSGrepFactory(processorService));
		createFactoryFunction(new JSFactory(processorService));
		createFactoryFunction(new CollectFactory(processorService));
		createFactoryFunction(new TailFactory(processorService));
		createFactoryFunction(new PrintFactory(processorService));
		createFactoryFunction(new StatFactory(processorService));
		createFactoryFunction(new DfFactory(processorService));
		createFactoryFunction(new DfFactory(processorService));
		createFactoryFunction(new TopFactory(processorService));
		createFactoryFunction(new AlertGeneratorFactory(processorService));
		createFactoryFunction(new OnceFactory(processorService));
		createFactoryFunction(new CurlFactory(processorService));
		createFactoryFunction(new CountFactory(processorService));
		createFactoryFunction(new TrailFactory(processorService));
		createFactoryFunction(new ReceiveFactory(processorService));
		createFactoryFunction(new PostFactory(processorService));
		createFactoryFunction(new CronFactory(processorService));
		createFactoryFunction(new JSCronFactory(processorService));
		createFactoryFunction(new ShFactory(processorService));
		createFactoryFunction(new JstackFactory(processorService));
	}

	public static Object eval(String str, String location) throws ScriptException {
		ScriptContext context = scriptEngine.getContext();
		context.setAttribute(ScriptEngine.FILENAME, location, ScriptContext.ENGINE_SCOPE);
		return scriptEngine.eval(str, context);
	}

	private void loadScriptResources() {
		try {
			// Load NPM first
			loadScriptResource(applicationContext.getResource("classpath:/scripts/npm/jvm-npm.js"));
			// Load the other scripts
			Resource[] scriptResources = applicationContext.getResources("classpath:/scripts/startup/*.js");
			for (Resource scriptResource : scriptResources) {
				loadScriptResource(scriptResource);
			}
		} catch (ScriptException e) {
			throw new AlerterException(e, ExceptionContext.makeToplevel());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void loadScriptResource(Resource scriptResource) throws IOException, ScriptException {
		logger.info("Loading script resource: " + scriptResource.getDescription());
		ScriptContext context = scriptEngine.getContext();
		context.setAttribute(ScriptEngine.FILENAME, scriptResource.getURI(), ScriptContext.ENGINE_SCOPE);
		scriptEngine.eval(new InputStreamReader(scriptResource.getInputStream()), context);
	}

	private void createFactoryFunction(com.untrackr.alerter.processor.common.ProcessorFactory processorFactory) {
		ScriptContext context = scriptEngine.getContext();
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put(processorFactory.name(), javascriptFunction(processorFactory::make));
	}

	public Processor loadProcessor(String filename) {
		try {
			ScriptContext context = scriptEngine.getContext();
			context.setAttribute(ScriptEngine.FILENAME, filename, ScriptContext.ENGINE_SCOPE);
			Object value = scriptEngine.eval(new FileReader(filename));
			if (!(value instanceof Processor)) {
				throw new AlerterException("value returned by script \"" + filename + "\" is not a processor: " + value, ExceptionContext.makeToplevel());
			}
			return (Processor) value;
		} catch (FileNotFoundException e) {
			throw new AlerterException("file not found: \"" + filename + "\"", ExceptionContext.makeToplevel());
		} catch (ScriptException e) {
			throw new AlerterException(e.getMessage(), ExceptionContext.makeToplevelScript(e));
		}
	}

	private <T> JavascriptFunction javascriptFunction(JavascriptFunction<Object, T> fun) {
		return fun;
	}

	@FunctionalInterface
	public interface JavascriptFunction<T, R> {
		R apply(T t) throws ScriptException;
	}

	public Object convertScriptValue(ValueLocation valueLocation, Type type, Object scriptValue, ExceptionContextFactory contextFactory) {
		if (scriptValue == null) {
			return null;
		} else if (type instanceof Class) {
			Class<?> clazz = (Class) type;
			if (clazz.isAssignableFrom(scriptValue.getClass())) {
				return scriptValue;
			} else if ((type == StringValue.class) && (String.class.isAssignableFrom(scriptValue.getClass()))) {
				return StringValue.makeConstant((String) scriptValue);
			} else if (scriptValue instanceof ScriptObjectMirror) {
				ScriptObjectMirror scriptObject = (ScriptObjectMirror) scriptValue;
				if ((type == JavascriptTransformer.class) && scriptObject.isFunction()) {
					return new JavascriptTransformer(scriptObject, valueLocation);
				} else if (type == JavascriptPredicate.class) {
					return new JavascriptPredicate(scriptObject, valueLocation);
				} else if (type == JavascriptProducer.class) {
					return new JavascriptProducer(scriptObject, valueLocation);
				} else if (type == StringValue.class){
					return StringValue.makeFunctional(new JavascriptTransformer(scriptObject, valueLocation), valueLocation);
				} else {
					Object value = BeanUtils.instantiate(clazz);
					BeanWrapperImpl wrapper = new BeanWrapperImpl(value);
					for (String propertyName : scriptObject.getOwnKeys(true)) {
						try {
							PropertyDescriptor descriptor = wrapper.getPropertyDescriptor(propertyName);
							Type fieldType = descriptor.getReadMethod().getGenericReturnType();
							String processorName = valueLocation.getProcessorName();
							ValueLocation propertyValueSource = ValueLocation.makeProperty(processorName, propertyName);
							wrapper.setPropertyValue(propertyName, convertScriptValue(propertyValueSource, fieldType, scriptObject.get(propertyName), contextFactory));
						} catch (InvalidPropertyException e) {
							throw new AlerterException("invalid property name \"" + propertyName + "\"", contextFactory.make());
						}
					}
					return value;
				}
			}
		} else {
			Type listType = parameterizedListType(type);
			if (listType != null) {
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
		}
		throw new AlerterException("invalid " + valueLocation.describeAsValue() + ", expected " + typeName(type) + ", got: " + scriptValue,
				contextFactory.make());
	}

	public interface ExceptionContextFactory {

		ExceptionContext make();

	}

	private String typeName(Type type) {
		if (type instanceof Class) {
			return simpleClassName((Class) type);
		}
		Type listType = parameterizedListType(type);
		if (listType != null) {
			return typeName(listType) + " array";
		} else {
			return type.toString();
		}
	}

	private String simpleClassName(Class<?> clazz) {
		if (String.class.isAssignableFrom(clazz)) {
			return "a string";
		} else if (Integer.class.isAssignableFrom(clazz)) {
			return "an integer";
		} else if (Number.class.isAssignableFrom(clazz)) {
			return "a number";
		} else if (JavascriptFunction.class.isAssignableFrom(clazz)) {
			return "a function";
		} else if (ProcessorDesc.class.isAssignableFrom(clazz)) {
			return "a processor descriptor";
		} else if (StringValue.class.isAssignableFrom(clazz)) {
			return "a string or a function";
		} else {
			return "a " + clazz.getSimpleName();
		}
	}

	private Type parameterizedListType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			Type[] args = paramType.getActualTypeArguments();
			if ((paramType.getRawType() == List.class) && (args.length == 1)) {
				return args[0];
			}
		}
		return null;
	}

	public NashornScriptEngine getScriptEngine() {
		return scriptEngine;
	}

}

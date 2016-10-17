package com.untrackr.alerter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.consumer.alert.AlertGeneratorFactory;
import com.untrackr.alerter.processor.consumer.post.PostFactory;
import com.untrackr.alerter.processor.filter.collect.CollectFactory;
import com.untrackr.alerter.processor.filter.grep.GrepFactory;
import com.untrackr.alerter.processor.filter.js.JSFactory;
import com.untrackr.alerter.processor.filter.jsgrep.JSGrepFactory;
import com.untrackr.alerter.processor.filter.jstack.JstackFactory;
import com.untrackr.alerter.processor.filter.once.OnceFactory;
import com.untrackr.alerter.processor.filter.print.EchoFactory;
import com.untrackr.alerter.processor.filter.sh.ShFactory;
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
import jdk.nashorn.api.scripting.NashornScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.*;
import java.io.FileReader;

@Service
public class FactoryService implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(FactoryService.class);

	@Autowired
	private ProcessorService processorService;

	private ObjectMapper objectMapper = new ObjectMapper();

	// Nashorn
	private NashornScriptEngine scriptEngine;

	@Override
	public void afterPropertiesSet() throws Exception {
		scriptEngine = (NashornScriptEngine) new ScriptEngineManager().getEngineByName("nashorn");
		createFactoryFunction(new ParallelFactory(processorService));
		createFactoryFunction(new PipeFactory(processorService));
		createFactoryFunction(new ConsoleFactory(processorService));
		createFactoryFunction(new GrepFactory(processorService));
		createFactoryFunction(new JSGrepFactory(processorService));
		createFactoryFunction(new JSFactory(processorService));
		createFactoryFunction(new CollectFactory(processorService));
		createFactoryFunction(new TailFactory(processorService));
		createFactoryFunction(new EchoFactory(processorService));
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

	private void createFactoryFunction(ProcessorFactory processorFactory) {
		ScriptContext context = scriptEngine.getContext();
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put(processorFactory.name(), nashornFunction(processorFactory::make));
	}

	public Processor loadProcessor(String filename) throws ScriptExecutionError {
		try {
			ScriptContext context = scriptEngine.getContext();
			context.setAttribute(ScriptEngine.FILENAME, filename, ScriptContext.ENGINE_SCOPE);
			Object value = scriptEngine.eval(new FileReader(filename));
			if (!(value instanceof Processor)) {
				throw new ScriptExecutionError("value returned by \"" + filename + "\" is not a processor", ScriptStack.emptyStack());
			}
			return (Processor) value;
		} catch (ScriptException e) {
			// Exception thrown by the javascript runtime.
			ScriptStack stack = ScriptStack.exceptionStack(e);
			if (stack.empty() && (e.getCause() != null)) {
				stack = ScriptStack.exceptionStack(e.getCause());
			}
			if (stack.empty()) {
				stack.addElement(e.getFileName(), e.getLineNumber());
			}
			throw new ScriptExecutionError(e.getLocalizedMessage(), stack);
		} catch (RuntimeScriptError e) {
			// Exception thrown by our code to signal an execution error.
			ScriptStack stack = ScriptStack.exceptionStack(e);
			ScriptStack.ScriptStackElement top = stack.top();
			String detailedMessage = e.getLocalizedMessage();
			if (top != null) {
				// Messages in ScriptException are of the form "XXX in YYY at line number ZZZ", so for consistency we
				// add file and line information to the detailed error message.
				detailedMessage = detailedMessage + " in " + top.getFileName() + " at line number " + top.getLineNumber();
			}
			throw new ScriptExecutionError(detailedMessage, ScriptStack.exceptionStack(e));
		} catch (Throwable t) {
			// Exception occurring somewhere in the code. This could be our code unexpectedly throwing an exception,
			// typically because of a bug as the code should always throw RuntimeScriptError to signal errors in a
			// disciplined way.
			throw new ScriptExecutionError(t.getMessage(), t, ScriptStack.exceptionStack(t));
		}
	}

	private <T> JsFunction nashornFunction(JsFunction<Object, T> fun) {
		return fun;
	}

	@FunctionalInterface
	public interface JsFunction<T, R> {
		R apply(T t) throws ScriptException;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public NashornScriptEngine getScriptEngine() {
		return scriptEngine;
	}

}

package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.producer.CommandRunner;
import com.untrackr.alerter.processor.producer.CommandExecutorDesc;
import com.untrackr.alerter.service.ProcessorService;
import jdk.nashorn.api.scripting.NashornScriptEngine;

import javax.script.CompiledScript;
import javax.script.ScriptException;

public abstract class ActiveProcessorFactory extends ProcessorFactory {

	public ActiveProcessorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected void initialize(ActiveProcessor processor, ActiveProcessorDesc processorDesc) {
		if (processorDesc.getName() != null) {
			processor.setName(processorDesc.getName());
		}
	}

	protected CompiledScript compileScript(JsonDescriptor jsonDescriptor, String field, String source) {
		NashornScriptEngine nashorn = processorService.scriptEngine();
		try {
			return nashorn.compile(source);
		} catch (ScriptException e) {
			throw new ValidationError("incorrect JavaScript code in \"" + field + "\" field: " + e.getLocalizedMessage(), jsonDescriptor);
		}
	}

	protected CommandRunner makeCommandOutputProducer(JsonDescriptor jsonDescriptor, CommandExecutorDesc descriptor) {
		String command = checkVariableSubstitution(jsonDescriptor, "command",
				checkFieldValue(jsonDescriptor, "command", descriptor.getGenerator()));
		return new CommandRunner(processorService, command);
	}

}

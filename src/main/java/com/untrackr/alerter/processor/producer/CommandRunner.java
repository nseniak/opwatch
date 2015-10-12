package com.untrackr.alerter.processor.producer;

import com.untrackr.alerter.ioservice.LineReader;
import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.processor.common.ActiveProcessor;
import com.untrackr.alerter.processor.common.Payload;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.RuntimeProcessorError;
import com.untrackr.alerter.service.ProcessorService;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandRunner {

	private ProcessorService processorService;
	private String command;
	private boolean commandExecutionErrorSignaled;
	private Process process;

	public CommandRunner(ProcessorService processorService, String command) {
		this.processorService = processorService;
		this.command = command;
		this.commandExecutionErrorSignaled = false;
	}

	public void startProcess(ActiveProcessor processor) {
		try {
			File currentDescDir = processor.getPath().last().getFile().getParentFile();
			String[] cmdArray = {"/bin/sh", "-c", command};
			process = Runtime.getRuntime().exec(cmdArray, null, currentDescDir);
		} catch (IOException e) {
			if (commandExecutionErrorSignaled) {
				return;
			} else {
				commandExecutionErrorSignaled = true;
				throw new RuntimeProcessorError(e, processor, null);
			}
		}
		commandExecutionErrorSignaled = false;
	}

	public void stopProcess() {
		if (process != null) {
			process.destroyForcibly();
		}
	}

	public void consume(Processor processor, Payload payload) {
		if (process == null) {
			throw new RuntimeProcessorError("command process not started", processor, payload);
		}
		if (!checkAlive(processor)) {
			throw new RuntimeProcessorError("command process has exited", processor, payload);
		}
		try {
			process.getOutputStream().write(payload.asText().getBytes());
			process.getOutputStream().write('\n');
			process.getOutputStream().flush();
		} catch (IOException e) {
			throw new RuntimeProcessorError(e, processor, payload);
		}
	}

	public void produce(ActiveProcessor processor, long exitTimeout) {
		AlerterProfile profile = processorService.getProfileService().profile();
		int bufferSize = profile.getLineBufferSize();
		try (LineReader lineReader = new LineReader(new BufferedInputStream(process.getInputStream()), bufferSize, true)) {
			while (true) {
				String line;
				long t0 = System.currentTimeMillis();
				while ((line = lineReader.readLine()) == null) {
					if (!checkAlive(processor)) {
						return;
					}
					if ((exitTimeout >= 0) && ((System.currentTimeMillis() - t0) > exitTimeout)) {
						throw new RuntimeProcessorError("timeout waiting for script output", processor, null);
					}
					Thread.sleep(profile.getCronScriptOutputCheckDelay());
				}
				CommandOutput output = new CommandOutput();
				output.script = command;
				output.text = line;
				processor.outputProduced(output);
			}
		} catch (IOException e) {
			throw new RuntimeProcessorError(e, processor, null);
		} catch (InterruptedException e) {
			// Nothing to do: exiting
			return;
		} finally {
			process.destroyForcibly();
			process = null;
		}
	}

	private boolean checkAlive(Processor processor) {
		if (process.isAlive()) {
			return true;
		}
		if ((process.exitValue() != 0) && (process.exitValue() != 130)) {
			String output = "";
			try {
				List<String> errorOutput = IOUtils.readLines(process.getErrorStream());
				if (!errorOutput.isEmpty()) {
					if (errorOutput.size() > 5) {
						errorOutput = errorOutput.subList(0, 5);
						errorOutput.add("...");
					}
					output = ", error output: " + String.join(" - ", errorOutput);
				}
			} catch (IOException e) {
				// Nothing to do
			}
			throw new RuntimeProcessorError("command exited with error status: " + process.exitValue() + output, processor, null);
		}
		return false;
	}

	public static class CommandOutput {

		private String script;
		private String text;

		public String getScript() {
			return script;
		}

		public String getText() {
			return text;
		}

	}

	public String getCommand() {
		return command;
	}

}

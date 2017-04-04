package com.untrackr.alerter.processor.primitives.producer;

import com.untrackr.alerter.ioservice.LineReader;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.AlerterProfile;
import com.untrackr.alerter.processor.payload.Payload;
import com.untrackr.alerter.service.ProcessorService;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandRunner {

	private ProcessorService processorService;
	private String command;
	private File directory;
	private Process process;

	public CommandRunner(ProcessorService processorService, String command, File directory) {
		this.processorService = processorService;
		this.command = command;
		this.directory = directory;
	}

	public void startProcess(ActiveProcessor processor) {
		try {
			String[] cmdArray = {"/bin/sh", "-c", command};
			process = Runtime.getRuntime().exec(cmdArray, null, directory);
		} catch (Exception e) {
			throw new RuntimeError("cannot run command: " + e.getMessage(), e,
					new ProcessorVoidExecutionContext(processor));
		}
	}

	public void stopProcess() {
		if (process != null) {
			process.destroyForcibly();
		}
	}

	public void consume(Processor processor, Payload payload) {
		if (process == null) {
			AlerterProfile profile = processorService.getProfileService().profile();
			long start = System.currentTimeMillis();
			while (process == null) {
				if ((System.currentTimeMillis() - start) > profile.getCommandStartTimeout()) {
					throw new RuntimeError("command process not started", new ProcessorVoidExecutionContext(processor));
				}
				try {
					Thread.sleep(profile.getCommandStartSleepTime());
				} catch (InterruptedException e) {
					// Shutting down.
					throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
				}
			}
		}
		if (!checkAlive(processor)) {
			throw new RuntimeError("command process has exited", new ProcessorPayloadExecutionContext(processor, payload));
		}
		try {
			process.getOutputStream().write(processorService.json(payload).getBytes());
			process.getOutputStream().write('\n');
			process.getOutputStream().flush();
		} catch (IOException e) {
			throw new RuntimeError("cannot write data to command process", e,
					new ProcessorPayloadExecutionContext(processor, payload));
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
						throw new RuntimeError("timeout waiting for command output",
								new ProcessorVoidExecutionContext(processor));
					}
					Thread.sleep(profile.getCronScriptOutputCheckDelay());
				}
				CommandOutput output = new CommandOutput();
				output.command = command;
				output.line = line;
				processor.outputProduced(output);
			}
		} catch (IOException e) {
			if (e.getMessage().equals("Stream closed")) {
				// The processor is being stopped. Just exit.
				throw new ApplicationInterruptedException(ApplicationInterruptedException.STREAM_CLOSED);
			} else {
				throw new RuntimeError("cannot read from command output", e,
						new ProcessorVoidExecutionContext(processor));
			}
		} catch (InterruptedException e) {
			// Shutting down.
			throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
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
			throw new RuntimeError("command exited with error status: " + process.exitValue() + output,
					new ProcessorVoidExecutionContext(processor));
		}
		return false;
	}

	public static class CommandOutput {

		private String command;
		private String line;

		public String getCommand() {
			return command;
		}

		public String getLine() {
			return line;
		}

	}

	public String getCommand() {
		return command;
	}

}

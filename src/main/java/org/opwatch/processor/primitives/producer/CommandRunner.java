package org.opwatch.processor.primitives.producer;

import com.google.common.io.CharStreams;
import org.opwatch.ioservice.LineReader;
import org.opwatch.processor.common.*;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.Config;
import org.opwatch.service.ProcessorService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
			throw new RuntimeError("cannot run command: " + e.getMessage(), new ProcessorVoidExecutionScope(processor), e);
		}
	}

	public void stopProcess() {
		if (process != null) {
			process.destroyForcibly();
		}
	}

	public void consume(Processor processor, Payload payload) {
		if (process == null) {
			Config profile = processorService.config();
			long start = System.currentTimeMillis();
			while (process == null) {
				if ((System.currentTimeMillis() - start) > profile.commandStartTimeout()) {
					throw new RuntimeError("command process not started", new ProcessorVoidExecutionScope(processor));
				}
				try {
					Thread.sleep(profile.commandStartSleepTime());
				} catch (InterruptedException e) {
					// Shutting down.
					throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
				}
			}
		}
		if (!checkAlive(processor)) {
			throw new RuntimeError("command process has exited", new ProcessorPayloadExecutionScope(processor, payload));
		}
		try {
			process.getOutputStream().write(processorService.getScriptService().stringify(payload.getValue()).getBytes());
			process.getOutputStream().write('\n');
			process.getOutputStream().flush();
		} catch (IOException e) {
			throw new RuntimeError("cannot write data to command process", new ProcessorPayloadExecutionScope(processor, payload), e);
		}
	}

	public void produce(ActiveProcessor processor, long exitTimeout) {
		Config profile = processorService.config();
		int bufferSize = profile.lineBufferSize();
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
								new ProcessorVoidExecutionScope(processor));
					}
					Thread.sleep(profile.cronScriptOutputCheckDelay());
				}
				processor.outputProduced(line);
			}
		} catch (IOException e) {
			if (e.getMessage().equals("Stream closed")) {
				// The processor is being stopped. Just exit.
				throw new ApplicationInterruptedException(ApplicationInterruptedException.STREAM_CLOSED);
			} else {
				throw new RuntimeError("cannot read from command output", new ProcessorVoidExecutionScope(processor), e);
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
				List<String> errorOutput = CharStreams.readLines(new InputStreamReader(process.getErrorStream()));
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
					new ProcessorVoidExecutionScope(processor));
		}
		return false;
	}

}

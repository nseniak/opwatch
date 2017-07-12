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

package org.opwatch.processor.primitives.producer;

import com.google.common.io.CharStreams;
import org.opwatch.ioservice.LineReader;
import org.opwatch.processor.common.*;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.Config;
import org.opwatch.service.ProcessorService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CommandRunner {

	private ProcessorService processorService;
	private CommandInfo commandInfo;
	private Process process;

	public CommandRunner(ProcessorService processorService, CommandInfo commandInfo) {
		this.processorService = processorService;
		this.commandInfo = commandInfo;
	}

	public void startProcess(ActiveProcessor processor) {
		try {
			String[] cmdArray = {"/bin/sh", "-c", commandInfo.getCommand()};
			process = Runtime.getRuntime().exec(cmdArray, null, commandInfo.getDirectory());
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
			Config config = processorService.config();
			long start = System.currentTimeMillis();
			while (process == null) {
				if ((System.currentTimeMillis() - start) > config.commandStartTimeout()) {
					throw new RuntimeError("command process not started", new ProcessorVoidExecutionScope(processor));
				}
				try {
					Thread.sleep(config.commandStartSleepTime());
				} catch (InterruptedException e) {
					// Shutting down.
					throw new ApplicationInterruptedException(ApplicationInterruptedException.INTERRUPTION);
				}
			}
		}
		if (!checkAlive(processor)) {
			throw new RuntimeError("input received but no command is currently running", new ProcessorPayloadExecutionScope(processor, payload));
		}
		try {
			process.getOutputStream().write(processorService.getScriptService().jsonStringify(payload.getValue()).getBytes());
			process.getOutputStream().write('\n');
			process.getOutputStream().flush();
		} catch (IOException e) {
			throw new RuntimeError("cannot write data to command process", new ProcessorPayloadExecutionScope(processor, payload), e);
		}
	}

	public void produce(ActiveProcessor processor, long exitTimeout) {
		Config config = processorService.config();
		int bufferSize = config.lineBufferSize();
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
					Thread.sleep(config.shScriptOutputCheckDelay());
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

	public boolean started() {
		return process != null;
	}

}

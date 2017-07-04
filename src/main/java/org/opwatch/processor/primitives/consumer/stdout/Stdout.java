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

package org.opwatch.processor.primitives.consumer.stdout;

import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.consumer.Consumer;
import org.opwatch.service.ProcessorService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Stdout extends Consumer<StdoutConfig> {

	private String file;
	private PrintWriter writer;

	public Stdout(ProcessorService processorService, StdoutConfig configuration, String name, String file) {
		super(processorService, configuration, name);
		this.file = file;
	}

	@Override
	public void start() {
		super.start();
		if (file != null) {
			processorService.withExceptionHandling("cannot create file " + file,
					() -> new ProcessorVoidExecutionScope(this),
					() -> {
						writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
					});
		}
	}

	@Override
	public void consume(Payload payload) {
		String output = processorService.getScriptService().jsonStringify(payload.getValue());
		if (writer == null) {
			processorService.printStdout(output);
		} else {
			writer.println(output);
			writer.flush();
		}
	}

}

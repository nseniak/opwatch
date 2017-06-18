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

package org.opwatch.processor.primitives.producer.tail;

import org.opwatch.ioservice.TailedFile;
import org.opwatch.service.Config;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.Producer;
import org.opwatch.service.ProcessorService;

import java.nio.file.Path;

public class Tail extends Producer<TailConfig> {

	private Path file;
	private boolean ignoreBlankLine;
	private TailedFile tailedFile;

	public Tail(ProcessorService processorService, TailConfig configuration, String name, Path file, boolean ignoreBlankLine) {
		super(processorService, configuration, name);
		this.file = file;
		this.ignoreBlankLine = ignoreBlankLine;
	}

	@Override
	public void start() {
		Config profile = getProcessorService().config();
		tailedFile = new TailedFile(profile, file, (line, lineNumber) -> {
			if (ignoreBlankLine && line.trim().isEmpty()) {
				return;
			}
			Payload payload = Payload.makeRoot(processorService, this, line);
			payload.setMetadata(new TailPayloadMetadata(file.toAbsolutePath().toString(), lineNumber));
			output(payload);
		});
		getProcessorService().getFileTailingService().addTailedFile(tailedFile);
	}

	@Override
	public void stop() {
		getProcessorService().getFileTailingService().removeTailedFile(tailedFile);
	}

}

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

package org.opwatch.processor.primitives.producer.stat;

import jdk.nashorn.internal.runtime.ScriptRuntime;
import org.opwatch.processor.common.SchedulingInfo;
import org.opwatch.processor.payload.PayloadPojoValue;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

import java.io.File;

public class Stat extends ScheduledProducer<StatConfig> {

	private File file;

	public Stat(ProcessorService processorService, StatConfig configuration, String name, SchedulingInfo schedulingInfo, File file) {
		super(processorService, configuration, name, schedulingInfo);
		this.file = file;
	}

	@Override
	protected void produce() {
		FileInfo info = new FileInfo();
		info.file = file.getAbsolutePath();
		if (!file.exists()) {
			info.exists = false;
		} else {
			info.exists = true;
			info.size = file.length();
			info.lastModified = file.lastModified();
		}
		outputProduced(info.toJavascript(processorService.getScriptService()));
	}

	public static class FileInfo extends PayloadPojoValue {

		private String file;
		private boolean exists;
		private Object size = ScriptRuntime.UNDEFINED;
		private Object lastModified = ScriptRuntime.UNDEFINED;

		public String getFile() {
			return file;
		}

		public boolean isExists() {
			return exists;
		}

		public Object getSize() {
			return size;
		}

		public Object getLastModified() {
			return lastModified;
		}

	}

}

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

package org.opwatch.processor.primitives.consumer.log;

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.OptionalProperty;
import org.opwatch.service.Config;

public class LogConfig extends ActiveProcessorConfig {

	private String file;
	private String maxSize = Config.defaultLogMaxSize();
	private String maxTotalSize = Config.defaultLogTotalMaxSize();
	private Integer maxHistory = Config.defaultLogMaxHistory();
	private String compression = Config.defaultLogCompression();

	@ImplicitProperty
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@OptionalProperty
	public String getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(String maxSize) {
		this.maxSize = maxSize;
	}

	@OptionalProperty
	public String getMaxTotalSize() {
		return maxTotalSize;
	}

	public void setMaxTotalSize(String maxTotalSize) {
		this.maxTotalSize = maxTotalSize;
	}

	@OptionalProperty
	public Integer getMaxHistory() {
		return maxHistory;
	}

	public void setMaxHistory(Integer maxHistory) {
		this.maxHistory = maxHistory;
	}

	@OptionalProperty
	public String getCompression() {
		return compression;
	}

	public void setCompression(String compression) {
		this.compression = compression;
	}

}

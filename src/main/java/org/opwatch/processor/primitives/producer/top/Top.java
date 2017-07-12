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

package org.opwatch.processor.primitives.producer.top;

import com.sun.management.OperatingSystemMXBean;
import org.opwatch.processor.common.SchedulingInfo;
import org.opwatch.processor.payload.PayloadPojoValue;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

import java.lang.management.ManagementFactory;

public class Top extends ScheduledProducer<TopConfig> {

	public Top(ProcessorService processorService, TopConfig configuration, String name, SchedulingInfo schedulingInfo) {
		super(processorService, configuration, name, schedulingInfo);
	}

	@Override
	protected void produce() {
		TopInfo info = new TopInfo();
		OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		info.availableProcessors = osBean.getAvailableProcessors();
		info.loadAverage = osBean.getSystemLoadAverage();
		info.totalSwapSpace = osBean.getTotalSwapSpaceSize();
		info.freeSwapSpace = osBean.getFreeSwapSpaceSize();
		info.totalPhysicalMemory = osBean.getTotalPhysicalMemorySize();
		info.freePhysicalMemory = osBean.getFreePhysicalMemorySize();
		outputProduced(info.toJavascript(processorService.getScriptService()));
	}

	public static class TopInfo  extends PayloadPojoValue {

		private int availableProcessors;
		private double loadAverage;
		private long totalSwapSpace;
		private long freeSwapSpace;
		private long totalPhysicalMemory;
		private long freePhysicalMemory;

		public int getAvailableProcessors() {
			return availableProcessors;
		}

		public double getLoadAverage() {
			return loadAverage;
		}

		public long getTotalSwapSpace() {
			return totalSwapSpace;
		}

		public long getFreeSwapSpace() {
			return freeSwapSpace;
		}

		public long getTotalPhysicalMemory() {
			return totalPhysicalMemory;
		}

		public long getFreePhysicalMemory() {
			return freePhysicalMemory;
		}

	}

}

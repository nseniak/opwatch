package com.untrackr.alerter.processor.producer.top;

import com.sun.management.OperatingSystemMXBean;
import com.untrackr.alerter.common.ScriptObject;
import com.untrackr.alerter.processor.producer.ScheduledExecutor;
import com.untrackr.alerter.processor.producer.ScheduledProducer;
import com.untrackr.alerter.service.ProcessorService;

import java.lang.management.ManagementFactory;

public class Top extends ScheduledProducer<TopDesc> {

	public Top(ProcessorService processorService, TopDesc descriptor, String name, ScheduledExecutor scheduledExecutor) {
		super(processorService, descriptor, name, scheduledExecutor);
	}

	@Override
	protected void produce() {
		TopInfo info = new TopInfo(processorService);
		OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		info.loadAverage = osBean.getSystemLoadAverage();
		info.totalSwapSpace = osBean.getTotalSwapSpaceSize();
		info.freeSwapSpace = osBean.getFreeSwapSpaceSize();
		info.totalPhysicalMemory = osBean.getTotalPhysicalMemorySize();
		info.freePhysicalMemory = osBean.getFreePhysicalMemorySize();
		outputProduced(info);
	}

	public static class TopInfo  extends ScriptObject {

		private double loadAverage;
		private long totalSwapSpace;
		private long freeSwapSpace;
		private long totalPhysicalMemory;
		private long freePhysicalMemory;

		public TopInfo(ProcessorService processorService) {
			super(processorService);
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

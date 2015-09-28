package com.untrackr.alerter.processor.producer;

import com.sun.management.OperatingSystemMXBean;
import com.untrackr.alerter.model.descriptor.IncludePath;
import com.untrackr.alerter.service.ProcessorService;

import java.lang.management.ManagementFactory;

public class Top extends ScheduledProducer {

	public Top(ProcessorService processorService, IncludePath path, ScheduledExecutor scheduledExecutor) {
		super(processorService, path, scheduledExecutor);
	}

	@Override
	protected Object produce() {
		TopInfo info = new TopInfo();
		OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		info.setLoadAverage(osBean.getSystemLoadAverage());
		info.setTotalSwapSpace(osBean.getTotalSwapSpaceSize());
		info.setFreeSwapSpace(osBean.getFreeSwapSpaceSize());
		info.setTotalPhysicalMemory(osBean.getTotalPhysicalMemorySize());
		info.setFreePhysicalMemory(osBean.getFreePhysicalMemorySize());
		return info;
	}

	@Override
	public String identifier() {
		return null;
	}

	public static class TopInfo {

		private double loadAverage;
		private long totalSwapSpace;
		private long freeSwapSpace;
		private long totalPhysicalMemory;
		private long freePhysicalMemory;

		public double getLoadAverage() {
			return loadAverage;
		}

		public void setLoadAverage(double loadAverage) {
			this.loadAverage = loadAverage;
		}

		public long getTotalSwapSpace() {
			return totalSwapSpace;
		}

		public void setTotalSwapSpace(long totalSwapSpace) {
			this.totalSwapSpace = totalSwapSpace;
		}

		public long getFreeSwapSpace() {
			return freeSwapSpace;
		}

		public void setFreeSwapSpace(long freeSwapSpace) {
			this.freeSwapSpace = freeSwapSpace;
		}

		public long getTotalPhysicalMemory() {
			return totalPhysicalMemory;
		}

		public void setTotalPhysicalMemory(long totalPhysicalMemory) {
			this.totalPhysicalMemory = totalPhysicalMemory;
		}

		public long getFreePhysicalMemory() {
			return freePhysicalMemory;
		}

		public void setFreePhysicalMemory(long freePhysicalMemory) {
			this.freePhysicalMemory = freePhysicalMemory;
		}

	}

}

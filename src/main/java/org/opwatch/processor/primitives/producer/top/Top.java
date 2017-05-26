package org.opwatch.processor.primitives.producer.top;

import com.sun.management.OperatingSystemMXBean;
import org.opwatch.processor.payload.PayloadPojoValue;
import org.opwatch.processor.primitives.producer.ScheduledExecutor;
import org.opwatch.processor.primitives.producer.ScheduledProducer;
import org.opwatch.service.ProcessorService;

import java.lang.management.ManagementFactory;

public class Top extends ScheduledProducer<TopConfig> {

	public Top(ProcessorService processorService, TopConfig configuration, String name, ScheduledExecutor scheduledExecutor) {
		super(processorService, configuration, name, scheduledExecutor);
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

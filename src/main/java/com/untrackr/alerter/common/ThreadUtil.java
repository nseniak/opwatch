package com.untrackr.alerter.common;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

	public static Logger logger = LoggerFactory.getLogger(ThreadUtil.class);

	/**
	 * @param basename
	 * @return thread factory with conveniently named thread
	 */
	public static ThreadFactory threadFactory(String basename) {
		return threadFactory(basename, false);
	}

	/**
	 * @param basename
	 * @param daemon
	 * @return thread factory with conveniently named thread
	 */
	public static ThreadFactory threadFactory(String basename, boolean daemon) {
		return new ThreadFactoryBuilder()
				.setNameFormat(basename + "-%d")
				.setDaemon(daemon)
				.build();
	}

	public static void safeExecutorShutdown(ThreadPoolExecutor executor, String name, long timeout) {
		executor.shutdown();
		logger.info("Waiting for executor termination: " + name);
		boolean terminated;
		try {
			terminated = executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.warn("Interruption while waiting for executor termination: " + name, e);
			return;
		}
		if (terminated) {
			logger.info("Executor terminated: " + name);
		} else {
			logger.warn("Executor termination timed out: " + name);
			safeExecutorShutdownNow(executor, name, timeout);
		}
	}

	public static void safeExecutorShutdownNow(ThreadPoolExecutor executor, String name, long timeout) {
		executor.shutdownNow();
		logger.info("Waiting for forced executor termination: " + name);
		boolean terminated;
		try {
			terminated = executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.warn("Interruption while waiting for forced executor termination: " + name, e);
			return;
		}
		if (terminated) {
			logger.info("Executor terminated: " + name);
		} else {
			logger.error("Timeout while waiting for executor forced termination: " + name);
		}
	}

	public static long randomizeTime(long time, double randomizationFactor) {
		double delta = randomizationFactor * time;
		double min = time - delta;
		double max = time + delta;
		long randomizedTime = Math.max(0, (long) (min + (Math.random() * (max - min + 1))));
		return randomizedTime;
	}

}

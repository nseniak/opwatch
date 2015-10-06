package com.untrackr;

import com.untrackr.alerter.service.ProcessorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import static com.untrackr.alerter.common.ApplicationUtil.checkProperty;

@SpringBootApplication
public class AlerterApplication {

	public static void main(String[] args) throws Exception {
		checkProperty("alerter.main");
		ApplicationContext context = SpringApplication.run(AlerterApplication.class, args);
		ProcessorService processorService = context.getBean(ProcessorService.class);
		processorService.startMainProcessor();
	}

}
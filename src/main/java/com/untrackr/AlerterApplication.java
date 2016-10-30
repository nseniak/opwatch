package com.untrackr;

import com.untrackr.alerter.service.ProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AlerterApplication implements CommandLineRunner {

	@Autowired
	private ProcessorService processorService;

	@Autowired
	private ApplicationContext applicationContext;

	@Override
	public void run(String... args) throws Exception {
		processorService.runCommandLine(args);
		SpringApplication.exit(applicationContext);
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(AlerterApplication.class, args);
	}

}
package com.untrackr.alerter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private ProcessorService processorService;

	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	public void pause() {
		processorService.stopMainProcessor();
	}

	@RequestMapping(value = "/start", method = RequestMethod.POST)
	public void resume() {
		processorService.startMainProcessor();
	}

	@RequestMapping(value = "/restart", method = RequestMethod.POST)
	public void restart() {
		processorService.stopMainProcessor();
		processorService.startMainProcessor();
	}

	@RequestMapping(value = "/exit", method = RequestMethod.POST)
	public void stop() {
		processorService.exit();
	}

	@RequestMapping("/healthcheck")
	public HealthcheckInfo healthcheck() {
		return processorService.healthcheck();
	}

}

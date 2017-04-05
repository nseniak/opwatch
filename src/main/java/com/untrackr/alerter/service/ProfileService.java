package com.untrackr.alerter.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class ProfileService {

	private AlerterConfig alerterConfig;

	@PostConstruct
	public void initAlerter() throws IOException {
		alerterConfig = new AlerterConfig();
	}

	public AlerterConfig profile() {
		return alerterConfig;
	}

}

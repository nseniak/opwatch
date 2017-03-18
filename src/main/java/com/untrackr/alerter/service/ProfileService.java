package com.untrackr.alerter.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class ProfileService {

	private AlerterProfile alerterProfile;

	@PostConstruct
	public void initAlerter() throws IOException {
		alerterProfile = new AlerterProfile();
	}

	public AlerterProfile profile() {
		return alerterProfile;
	}

}

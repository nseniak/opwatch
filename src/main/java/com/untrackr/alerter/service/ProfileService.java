package com.untrackr.alerter.service;

import com.untrackr.alerter.model.common.AlerterProfile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ProfileService {

	private AlerterProfile alerterProfile;

	@PostConstruct
	public void initAlerter() {
		alerterProfile = new AlerterProfile();
	}

	public AlerterProfile profile() {
		return alerterProfile;
	}

}

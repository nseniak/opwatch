package com.untrackr.alerter.model.common;

import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;

import java.util.List;

public class PushoverSettings {

	private List<Application> applications;
	private List<Group> groups;

	public PushoverKey makeKey(String applicationName, String groupName) {
		Application application = findApplication(applicationName);
		if (application == null) {
			throw new RuntimeException("Pushover application not found: " + applicationName);
		}
		PushoverSettings.Group group = findGroup(groupName);
		if (group == null) {
			throw new RuntimeException("Pushover group not found: " + groupName);
		}
		return new PushoverKey(application.getApiToken(), group.getUserKey());
	}

	public Application findApplication(String applicationName) {
		return applications.stream()
				.filter(application -> application.getName().equals(applicationName))
				.findFirst().orElse(null);
	}

	public Group findGroup(String groupName) {
		return groups.stream()
				.filter(group -> group.getName().equals(groupName))
				.findFirst().orElse(null);
	}

	public static class Application {

		private String name;
		private String apiToken;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getApiToken() {
			return apiToken;
		}

		public void setApiToken(String apiToken) {
			this.apiToken = apiToken;
		}

	}

	public static class Group {

		private String name;
		private String userKey;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUserKey() {
			return userKey;
		}

		public void setUserKey(String userKey) {
			this.userKey = userKey;
		}

	}

	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

}

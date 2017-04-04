package com.untrackr.alerter.channel.pushover;

import com.untrackr.alerter.channel.common.ServiceConfiguration;

import java.util.List;

public class PushoverConfiguration extends ServiceConfiguration {

	private List<Application> applications;
	private List<User> users;
	private List<ChannelConfig> channels;
	private Integer emergencyRetry;
	private Integer emergencyExpire;
	private Integer maxPerMinute;
	private Integer globalMaxPerMinute;

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

	public static class User {

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

	public static class ChannelConfig {

		private String name;
		private String application;
		private String user;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

	}

	public List<Application> getApplications() {
		return applications;
	}

	public void setApplications(List<Application> applications) {
		this.applications = applications;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<ChannelConfig> getChannels() {
		return channels;
	}

	public void setChannels(List<ChannelConfig> channels) {
		this.channels = channels;
	}

	public Integer getEmergencyRetry() {
		return emergencyRetry;
	}

	public void setEmergencyRetry(Integer emergencyRetry) {
		this.emergencyRetry = emergencyRetry;
	}

	public Integer getEmergencyExpire() {
		return emergencyExpire;
	}

	public void setEmergencyExpire(Integer emergencyExpire) {
		this.emergencyExpire = emergencyExpire;
	}

	public Integer getMaxPerMinute() {
		return maxPerMinute;
	}

	public void setMaxPerMinute(Integer maxPerMinute) {
		this.maxPerMinute = maxPerMinute;
	}

	public Integer getGlobalMaxPerMinute() {
		return globalMaxPerMinute;
	}

	public void setGlobalMaxPerMinute(Integer globalMaxPerMinute) {
		this.globalMaxPerMinute = globalMaxPerMinute;
	}

}

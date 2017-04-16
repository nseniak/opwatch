// Initialization code loaded on startup
// Use --init <file-or-url> to specify a different initialization file

load("script/processors.js");

// Definition of the message channels
channels = {

	"services": {

		// Pushover channels
		"pushover": {
			"channels": {
				"<unique channel name>": {
					"apiToken": "<your Pushover API token here>",
					"userKey": "<your Pushover user key here>"
				}
			}
		},

		// Slack channels
		"slack": {
			"channels": {
				"<unique channel name>": {
					"webhookUrl": "<your Slack webhook here>"
				}
			}
		},

		// Remote channels
		"remote": {
			"channels": {
				"<unique channel name>": {
					"hostname": "<remote host name>",
					// "port": <remote host port number (optional)>
					"channel": "<remote channel name>"
				}
			}
		}

	},


	// Default channel for alerts
	"alertChannel": "<channel name>",
	// Default channel for system messages (like errors)
	"systemChannel": "<channel name>"

};

// Uncomment the following line to make the channel definition effective:
// config.channels(channels);

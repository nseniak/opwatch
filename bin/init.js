// Initialization code loaded on startup
// Use --init <file-or-url> to specify a different initialization file

load("script/processors.js");

// Definition of the message channels
channels = {

	"services": {

		// Console channel
		"console": {
			"channels": {
				"console": {}
			}
		}

		/*

		// Complete missing information and uncomment to add channels

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

	 */

	},

	// Default channel for alerts
	"applicationChannel": "console",
	// Default channel for system messages (e.g., errors)
	"systemChannel": "console"

};

config.channels(channels);

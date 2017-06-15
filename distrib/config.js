// Initialization code loaded on startup
// Use --config <file-or-url> to specify a different initialization file

procs = require("processors");

// Definition of the message channels
channels = {

	"services": {

		/*

		// Complete missing information and uncomment to add channels

		// Pushover channels (see https://pushover.net/)
		"pushover": {
			"channels": {
				"<unique channel name>": {
					"apiToken": "<your Pushover API token here>",
					"userKey": "<your Pushover user key here>"
				}
			}
		},

		// Slack channels (see https://slack.com/)
		"slack": {
			"channels": {
				"<unique channel name>": {
					"webhookUrl": "<your Slack webhook here>"
				}
			}
		},

		// Remote channels; connected to channels of remote Opwatch instances
		"remote": {
			"channels": {
				"<unique channel name>": {
					"hostname": "<host on which the remote Opwatch instance is running>",
					"port": <http port of the remote Opwatch instance)>,
					"channel": "<name of the channel on the remote Opwatch instance>"
				}
			}
		}

	 */

	},

	// Default channel for alerts triggered by the "alert" processor. This channel can be overridden for in alert
	// processors using the "channel" configuration property.
	"applicationChannel": "console",
	// Channel used for alerts triggered by the Opwatch framework (e.g., informational messages, errors)
	"systemChannel": "console",
	// Channel used for application and system alerts when the default channel failed, e.g., when the
	// message service is down or its configuration is bogus.
	"fallbackChannel": "console"

};

config.channels(channels);

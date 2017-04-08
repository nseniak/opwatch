// Initialization code loaded on startup
// Use -init <file-or-url> to specify a different initialization file

load("script/processors.js");

// Definition of the message channels
channels = {

	"services": {

		// Pushover channels
		"pushover": {
			"channels": {
				"pushover_dev": {
					"apiToken": "<your Pushover API token here>",
					"userKey": "<your Pushover user key here>"
				}
			}
		}

	},

	// Use pushover_dev as the default alert channel
	"alert": "pushover_dev",
	// Use pushover_dev as the system channel
	"system": "pushover_dev"

};

// Uncomment the following line to make the channel definition effective:
// config.channels(channels);

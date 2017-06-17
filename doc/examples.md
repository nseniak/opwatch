# Example index

This page contains all the code examples provided in the documentation. Click a title to go to the page that includes
the example and see it in context.

<!-- example-list-begin -->
## [Display an alert if disk usage is > 80%; version 1](reference/processor/alert.md#display-an-alert-if-disk-usage-is--80-version-1)

```js
pipe(
	df("/tmp"), 
	alert({
	  title: "not enough space left",
	  trigger: function (dfOutput) { 
	  	return dfOutput.usageRatio > .8; 
	  },
	  toggle: true
	})
).run();
```

Since the `alert` processor doesn't have a `details` callback, the output of [`df`](df.md) is used for the displayed
alert details.

## [Display an alert if disk usage is > 80%; version 2](reference/processor/alert.md#display-an-alert-if-disk-usage-is--80-version-2)

```js
pipe(
	df("/tmp"), 
	alert({
	  title: "not enough space left",
	  details: function (dfOutput) { 
	  	return dfOutput.usageRatio; 
	  },
	  trigger: function (dfOutput) { 
	  	return dfOutput.usageRatio > .8; 
	  },
    toggle: true
  })
).run();
```
The `details` callback returns the usage ratio, so this ratio is displayed as the alert details rather than the
output of [`df`](df.md).

## [Display an alert if disk usage is > 80%; version 3](reference/processor/alert.md#display-an-alert-if-disk-usage-is--80-version-3)

```js
pipe(
	df("/tmp"), 
	alert({
	  title: "not enough space left",
	  details: "/tmp",
	  trigger: function (dfOutput) {
	  	return dfOutput.usageRatio > .8; 
	  },
    toggle: true
  })
).run();
```

The `details` property is defined as the string `"/tmp"` and therefore this string is displayed as the alert details.

## [Trigger an alert if the average free swap space over 5 minutes is smaller than 10 megabytes](reference/processor/apply.md#trigger-an-alert-if-the-average-free-swap-space-over-5-minutes-is-smaller-than-10-megabytes)

```js
pipe(
  top(),
  apply(function (topOutput) { return topOutput.freeSwapSpace; }),
  trail("5m"),
  alert({
  	title: "free swap space is low",
  	trigger: function (freeSwapSpaceTrail) { return stats(freeSwapSpaceTrail).mean < 1e7; },
  	toggle: true
  })
).run();
```

## [Print non-empty log file lines](reference/processor/apply.md#print-nonempty-log-file-lines)

```js
pipe(
  tail("application.log"),
  apply(function (line) { 
  	if (line.length !== 0) return line;
  	// Otherwise undefined is returned
  }),
  stdout()
).run();
```

## [Count seconds](reference/processor/call.md#count-seconds)

```js
var count = 0;

pipe(
  call(function () { return count++; }),
  stdout()
).run();
```

## [Every hour, publish the number of lines containing SIGNUP that were added to a log file](reference/processor/call.md#every-hour-publish-the-number-of-lines-containing-signup-that-were-added-to-a-log-file)

```js
var count = 0;

pipe(
  tail("application.log"),
  grep(/SIGNUP/),
  call(
  		{
  			input: function () { count++; },
  			output: function () { 
  				var result = count;
  				count = 0;
  				return result; 
  			},
  			delay: "1h",
  			period: "1h"
  		}
  ),
  alert({
  	title: "New lines in log file during the last hour",
  	level: "low"
  })
).run();
```

## [Trigger an alert if three consecutive http requests at a 30 second interval are unsuccessful](reference/processor/collect.md#trigger-an-alert-if-three-consecutive-http-requests-at-a-30-second-interval-are-unsuccessful)

```js
pipe(
		curl({ url: "http://www.mywebsite.com", period: "30s" }),
		collect(3),
		alert({
			title: "Website is down",
			trigger: function (curlOutputArray) {
				return curlOutputArray.every(function (seriesObject) {
					return seriesObject.value.status != 200;
				});
			},
			toggle: true
		})
).run();
```

## [Trigger an alert if a Web site is down](reference/processor/curl.md#trigger-an-alert-if-a-web-site-is-down)

```js
pipe(
  curl("https://httpbin.org"), 
  test(function (curlOutput) { return curlOutput.status != 200; }), 
  alert("Website is down")
).run();
```

## [Trigger an alert if a Web site is found to be down for at least 2 of the 10 last requests](reference/processor/curl.md#trigger-an-alert-if-a-web-site-is-found-to-be-down-for-at-least-2-of-the-10-last-requests)

```js
pipe(
  curl("https://httpbin.org"), 
  collect(10), 
  alert({
  	title: "Website is wobbly or down",
  	trigger: function (curlOutputArray) {
  		var down = 0;
  		for (var i = 0; i < curlOutputArray.length; i++) {
  			if (curlOutputArray[i].status != 200) down++;
  		}
  		return down >= 2;
  	},
  	toggle: true
  })
).run();
```

## [Trigger an alert if a REST API returns a wrong value](reference/processor/curl.md#trigger-an-alert-if-a-rest-api-returns-a-wrong-value)


```js
pipe(
  curl({
  	url: "https://jsonplaceholder.typicode.com/posts",
  	method: "post",
  	data: {
        name: 'foo',
        body: 'bar',
        userId: 1
      }
  }), 
  alert({
  	title: "API is down or buggy",
  	trigger: function (curlOutput) {
  		return (curlOutput.status != 201)
  			|| !curlOutput.json
  			|| (curlOutput.json.name !== "foo");
  	},
  	toggle: true
  })
).run();
```

## [Trigger an alert when a filesystem usage ratio is greater than 80%](reference/processor/df.md#trigger-an-alert-when-a-filesystem-usage-ratio-is-greater-than-80)

```js
pipe(
	df(),
	alert({
		title: "Filesystem is near full",
		trigger: function (dfOutput) {
			return dfOutput.usageRatio > 0.8;
		},
		toggle: true
	})
).run();
```

## [Trigger an alert for each line in a log file that contains `error` or `warning`](reference/processor/grep.md#trigger-an-alert-for-each-line-in-a-log-file-that-contains-error-or-warning)

```js
pipe(
  tail("application.log"), 
  grep(/error|warning/i), 
  alert("Problem found!")
).run();
```

## [Trigger an alert for each line in a log file that does *not* contain `info`](reference/processor/grep.md#trigger-an-alert-for-each-line-in-a-log-file-that-does-not-contain-info)

```js
pipe(
  tail("application.log"), 
  grep({ regexp: /info:/, invert: true }), 
  alert("Problem found!")
).run();
```

## [Trigger an alert if a json object log contains an error](reference/processor/json.md#trigger-an-alert-if-a-json-object-log-contains-an-error)

This example assumes that an application appends Json objects to the structured log file `application.log.json`: 

```js
pipe(
  tail("application.log.json"),
  json(),
  alert({
  	title: "error occurred in the log",
  	trigger: function (logObject) { return logObject.level === "ERROR"; }
  })
).run();
```

## [Trigger an alert if the file `application.log` contains `ERROR` or `WARNING`](reference/processor/parallel.md#trigger-an-alert-if-the-file-applicationlog-contains-error-or-warning)

```js
pipe(
  tail("application.log"), 
  parallel(grep(/ERROR/), grep(/WARNING/)), 
  alert("Error or warning")
).run();
```

## [Trigger an alert if either `application1.log` or `application2.log` contains `ERROR`](reference/processor/parallel.md#trigger-an-alert-if-either-application1log-or-application2log-contains-error)

```js
pipe(
	parallel(
			tail("application1.log"),
			tail("application2.log")
	),
  grep(/ERROR/), 
  alert("Error or warning")
).run();
```

## [Centralize logs from several servers and trigger an alert if they contain too many errors overall](reference/processor/send.md#centralize-logs-from-several-servers-and-trigger-an-alert-if-they-contain-too-many-errors-overall)

Execute on the servers running the application:

```js
pipe(
	tail("application.log"),
	send({ hostname: "centralizer.mydomain.com", path: "countLogErrors" })
).run();
```

Execute on `centralizer.mydomain.com`:

```js
pipe(
	receive("countLogErrors"),
	grep(/ERROR/),
	trail("10s"),
	alert({
		title: "Too many errors overall",
		trigger: function (trailOutput) { return trailOutput.length > 10; },
		toggle: true
	})
).run();
```

## [Centralize system load information from several servers and trigger an alert if the average CPU is too high](reference/processor/send.md#centralize-system-load-information-from-several-servers-and-trigger-an-alert-if-the-average-cpu-is-too-high)

Run on the servers running the application:

```js
pipe(
	top(),
	send({ hostname: "centralizer.mydomain.com", path: "checkCpu" })
).run();
```

Run on `centralizer.mydomain.com`:

```js
pipe(
	receive("checkCpu"),
	apply(function (topOutput, payload) {
		// Create an object associating the hostname (retrieved from the payload) and its topOutput
		return { 
			hostname: payload.hostname,
			topOutput: topOutput
		};
	}),
	trail("1m"),
	alert({
		title: "Average CPU too high",
		trigger: function (trailOutput) {
			if (trailOutput.length == 0) {
				return false;
			}
			// Keep the most recent top info for each server from which we've received info during the last minute 
			var serverInfo = {};
			for (var i = 0; i < trailOutput.length; i++) {
				var value = trailOutput[i].value;
				serverInfo[value.hostname] = value.topOutput; 
			}
			// Compute the CPU 
			var processors = 0;
			var loadAverage = 0;
			for (var hostname in serverInfo) {
				processors = processors + serverInfo[hostname].availableProcessors;
				loadAverage = loadAverage + serverInfo[hostname].loadAverage;
			}
			var cpu = loadAverage / processors;
			return cpu > .8; 
		},
		toggle: true
	})
).run();
```

## [Triggers an alert if the number of files in a directory is greater than 100](reference/processor/sh.md#triggers-an-alert-if-the-number-of-files-in-a-directory-is-greater-than-100)

Note the use of `json` to convert the string output of `sh` to a number: 

```js
pipe(
	sh("ls /tmp | wc -l"),
  json(),
  alert({
  	title: "Too many files",
  	trigger: function (count) { return count > 100; },
  	toggle: true
  })
).run();
```

## [Trigger an alert if there's no `mongod` running process](reference/processor/sh.md#trigger-an-alert-if-theres-no-mongod-running-process)

```js
pipe(
	sh("pgrep -q mongod && echo UP || echo DOWN"),
  alert({
  	title: "mongod is down",
  	trigger: function (shOutput) { return shOutput === "DOWN"; },
  	toggle: true
  })
).run();
```

Note how the `sh` command is designed to generate an output both in case of success (`UP`) and in case of failure 
(`DOWN`), which allows us to use a toggle alert that will start when the `mongod` process doesn't exist and will end 
when the `mongod` process is restarted. This is to be contrasted with the less good approach below:

```js
// Not great: triggers an alert every second as long as mongod is down. 
pipe(
	sh("pgrep -q mongod || echo DOWN"),
  alert({
  	title: "mongod is down",
  })
).run();
```

## [Use the `grep` unix command to filter payload](reference/processor/sh_f.md#use-the-grep-unix-command-to-filter-payload)

```js
pipe(
  tail("application.log"),
  sh_f("grep --line-buffered \"pattern\""),
  alert({
  	title: "error in log",
  })
).run();
```

The `--line-buffered` option forces `grep`'s output to be line buffered. By default, `grep`'s output is 
block buffered when standard output is not a terminal, which is the case here; without this option, the output 
would be delayed until the buffer is full, which would also delay the alarm. In general, you should use
shell commands with buffered output with care, as they might not yield to the natural behavior you would expected.

## [Trigger an alert when a file hasn't been updated since 10 minutes](reference/processor/stat.md#trigger-an-alert-when-a-file-hasnt-been-updated-since-10-minutes)

```js
pipe(
	stat("application.log"),
	alert({
		title: "Log file is inactive",
		trigger: function (statOutput) {
			if (!statOutput.exists) {
				return false;
			}
			var now = (new Date()).getTime();
			var minutes10 = 10 * 60 * 1000;
			return (now - statOutput.lastModified) > minutes10;
		},
		toggle: true
	})
).run();
```

## [Write system load information to standard output](reference/processor/stdout.md#write-system-load-information-to-standard-output)

```js
pipe(
	top(),
	stdout()
).run();
```

## [Trigger an alert when the log file gets bigger than 100,000 lines](reference/processor/tail.md#trigger-an-alert-when-the-log-file-gets-bigger-than-100000-lines)

```js
pipe(
	tail("application.log"),
	alert({
		title: "Log file has too many lines",
		trigger: function (trailOutput, trailPayload) { return trailPayload.metadata.line > 100000; },
		toggle: true
	})
).run();
```

## [Trigger an alert when the CPU is higher than 80%](reference/processor/top.md#trigger-an-alert-when-the-cpu-is-higher-than-80)

```js
pipe(
	top(),
	alert({
		title: "CPU is too high",
		trigger: function (topOutput) {
			var cpu = topOutput.loadAverage / topOutput.availableProcessors;
			return cpu > 0.8;
		},
		toggle: true
	})
).run();
```

## [Trigger an alert when a log file has more than 20 Java exception per second](reference/processor/trail.md#trigger-an-alert-when-a-log-file-has-more-than-20-java-exception-per-second)

This alert is in toggle mode, thus it will stay up as long as the exception frequency stays high:

```js
pipe(
	tail("application.log"),
	jstack(),
	trail("10s"),
	alert({
		title: "Too many exceptions",
		trigger: function (trailOutput) { return trailOutput.length > 20; },
		toggle: true
	})
).run();
```

## [Trigger an alert when the disk usage increases by more than 20% in an hour](reference/processor/trail.md#trigger-an-alert-when-the-disk-usage-increases-by-more-than-20-in-an-hour)

```js
pipe(
	df("/tmp"),
	trail("1h"),
	alert({
		title: "/tmp usage grew by more than 20% during the last hour",
		trigger: function (trail) { 
			var len = trail.length;
			return (len >= 2) && (trail[len - 1].usage / trail[0].usage) > 1.2; 
		},
		toggle: true
	})
).run();
```

Since `df` generates an output every second, the trail collected over an hour contains 3600
elements. Since only the first and the last elements are actually used to compute the alert trigger, the generation
period of `df` can be decreased, which will reduce Opwatch memory consumption:

```js
df({ file: "/tmp", period: "10m" })
```

## [Trigger an alert when a log file remains silent for more than 10 minutes](reference/processor/trail.md#trigger-an-alert-when-a-log-file-remains-silent-for-more-than-10-minutes)

```js
pipe(
	tail("application.log"),
	trail("10m"),
	alert({
		title: "Log file is silent",
		trigger: function (trailOutput) { return trailOutput.length == 0; },
		toggle: true
	})
).run();
```

<!-- example-list-end -->

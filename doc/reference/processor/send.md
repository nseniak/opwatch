## `send`

Sends its input to a remote Opwatch instance.

### Input and output

* Category: Consumer
* Input: Any value or object
* Output: None

### Synopsis

```js
send(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `hostname` | hostname running the remote Opwatch instance | String | *Mandatory* | 
| `path` | remote Opwatch instance `receive` path | String | *Mandatory* | 
| `port` | remote Opwatch instance HTTP port | Number | `28018` | 

### Description

The `send` processor sends its input to a remote Opwatch instance, which is expected to be running a `receive` 
processor with the same `path` value.

The input value or object is serialized using `JSON.stringify` and deserialized by the receiving instance using 
`JSON.parse`, so any part of the the input that cannot be fully serialized to Json is lost.

### Example

#### Centralize logs from several servers and trigger an alert if they contain too many errors overall

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

#### Centralize system load information from several servers and trigger an alert if the average CPU is too high

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

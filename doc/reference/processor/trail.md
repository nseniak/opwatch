## `trail`

Collects the inputs received during the last `duration` milliseconds.

### Input and output

* Category: Filter
* Input: Any
* Output: Array of SeriesObject objects

### Synopsis

```js
trail(duration)
trail(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `duration` | duration over which inputs are collected | Number of milliseconds or String | *mandatory* |
| `period` | period at which the output array is produced | Number of milliseconds or String | `1s"` |
 
 ### Output object
 
 The `trail` processor generates an array of SeriesObject objects representing the inputs received during
 the last `duration` milliseconds, ordered from oldest to newest. Each SeriesObject object has the following fields:
 
| Property | Description | Type |
| :--- | :--- | :--- | :--- |
| `value` | the value of the input | Object |
| `timestamp` | time at which the input was received | Number |

### Description

The `trail` processor generates an array of SeriesObject objects representing the inputs it has received
during the last `duration` milliseconds. This array is generated as a regular interval, defined by the `period`
property, which defaults to 1 second. The first array is generated `duration` milliseconds after the processor 
has started running. 
 
For instance, if `duration` is `3000` and `period` is `"1s"`, then `trail` first waits for 3 seconds then
starts generating every second an array of the inputs it has received during the last 3 seconds. 
 
Both `duration` and `period` can be expressed as either a number of milliseconds or a String representing an 
[ISO-8601 duration](https://en.wikipedia.org/wiki/ISO_8601#Durations), like for example `"30s"` for 30 seconds or
`"1m"` for one minute.

### Examples

#### Trigger an alert when a log file has more than 20 Java exception per second

This alert is in toggle mode, thus it will stay up as long as the exception frequency stays high:

```js
pipe(
	tail("application.log"),
	jstack(),
	trail("10s"),
	alert({
		title: "Too many exceptions",
		trigger: function (input) { return input.length > 20; },
		toggle: true
	})
).run();
```

#### Trigger an alert when the disk usage increases by more than 20% in an hour

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

#### Trigger an alert when a log file remains silent for more than 10 minutes

```js
pipe(
	tail("application.log"),
	trail("10m"),
	alert({
		title: "Log file is silent",
		trigger: function (input) { return input.length == 0; },
		toggle: true
	})
).run();
```
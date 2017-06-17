## `trail`

Collects the inputs received during the last `duration` milliseconds.

### Input and output

* Category: Filter
* Input: Any value or object
* Output: Array of [SeriesObject](../programming.md#seriesobject) objects

### Synopsis

```js
trail(duration)
trail(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `duration` | duration over which inputs are collected | Duration value | *Mandatory* |
| `delay` | initial delay after which the output is first generated | [Duration](../programming.md#Durations) | `"0s"` |
| `period` | period at which the output is repeatedly generated | [Duration](../programming.md#Durations) | `"10s"` |
 
### Output array
 
The `trail` processor generates an array of SeriesObject objects representing the inputs received during
the last `duration` milliseconds, ordered from oldest to newest.

### Description

The `trail` processor generates an array of [SeriesObject](../programming.md#seriesobject) objects representing the inputs it has received
during the last `duration` milliseconds. This array is generated as a regular interval, defined by the `period`
property, which defaults to 10 seconds. The first array is generated `duration` milliseconds after the processor 
has started running. 
 
For instance, if `duration` is `30000` and `period` is `"20s"`, then `trail` first waits for 30 seconds then
starts generating every 20 seconds an array of the inputs it has received during the last 30 seconds.
 
If `delay` is defined, the first output is generated after max(`delay`, `duration`) milliseconds.
 
### Examples

<!-- example-begin -->
#### Trigger an alert when a log file has more than 20 Java exception per second

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
<!-- example-end -->

<!-- example-begin -->
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
<!-- example-end -->

<!-- example-begin -->
#### Trigger an alert when a log file remains silent for more than 10 minutes

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
<!-- example-end -->

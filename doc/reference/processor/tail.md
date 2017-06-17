## `tail`

Outputs lines added to a file.

### Input and output

* Category: Producer
* Input: None
* Output: String

### Synopsis

```js
tail(file)
tail(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `file` | file to tail | String | *Mandatory* |
| `ignoreBlank` | when true, empty lines or line consisting only of space characters are ignored | Boolean | `false` |
 
### Description

The `tail` processor outputs the new lines that are added to a file. If the file doesn't exist when the processor is
started or is deleted after it is started, `tail` silently waits util it is created. The behavior of this processor 
is similar to the `tail -F -n 0` Unix command.

### Payload metadata

In addition to its output, `tail` produces a metadata object that has the following properties:

| Property | Description | Type | Presence | 
| :--- | :--- | :--- | :--- |
| `file` | the absolute path of the tailed file | String | *Always* |
| `line` | line number in the tailed file | Number | *Always* |

This object can be accessed by the `metadata` property of the [payload](processors.md) object.

### Examples

<!-- example-begin -->
#### Trigger an alert when the log file gets bigger than 100,000 lines

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
<!-- example-end -->

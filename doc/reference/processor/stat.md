## `stat`

Outputs file information.

### Input and output

* Category: Producer
* Input: None
* Output: FileInfo object

### Synopsis

```js
stat(file)
stat(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `file` | file to probe | String | *Mandatory* |
| `delay` | initial delay after which the file is first probed | [Duration](../programming.md#Durations) | `"0s"` |
| `period` | period at which the file is repeatedly probed | [Duration](../programming.md#Durations) | `"10s"` |
 
 ### FileInfo object
 
 The FileInfo object has the following properties:
 
| Property | Description | Type | Presence | 
| :--- | :--- | :--- | :--- |
| `file` | absolute path of the file | Object | *Always* |
| `exists` | true if the file exists, false otherwise | Boolean | *Always* |
| `size` | file size, in bytes | Number | *Optional* |
| `lastModified` | last modification time, measured in milliseconds since the epoch (00:00:00 GMT, January 1, 1970) | Number | *Optional* |

### Description

The `stat` processor generates file information at a regular interval.
 
### Examples

<!-- example-begin -->
#### Trigger an alert when a file hasn't been updated since 10 minutes

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
<!-- example-end -->

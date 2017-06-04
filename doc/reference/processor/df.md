## `df`

Outputs free disk space info.

### Input and output

* Category: Producer
* Input: None
* Output: FilesystemInfo object

### Synopsis

```js
df(file)
df(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `file` | determines the filesystem to probe | String | *Mandatory* |
| `period` | period at which an output is generated | [Duration](../programming.md#Durations) | `"1s"` |
 
 ### FilesystemInfo object
 
 The FilesystemInfo object has the following properties:
 
| Property | Description | Type | Presence | 
| :--- | :--- | :--- | :--- |
| `file` | absolute path of the file | Object | *Always* |
| `size` | total size of the filesystem, in bytes| Number | *Always* |
| `used` | filesystem used space, in bytes | Number | *Always* |
| `available` | filesystem available space, in bytes | Number | *Always* |
| `usageRatio` | `used` / `size` | Number | *Always* |

### Description

The `df` processor generates file system information at a regular interval.
 
### Examples

#### Trigger an alert when a filesystem usage ratio is greater than 80%

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
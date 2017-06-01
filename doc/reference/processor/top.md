## `top`

Outputs system load information.

### Input and output

* Category: Producer
* Input: None
* Output: TopInfo object

### Synopsis

```js
top()
top(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `period` | period at which an output is generated | Duration value | `1s"` |
 
 ### FilesystemInfo object
 
 The FilesystemInfo object has the following properties:
 
| Property | Description | Type | Presence | 
| :--- | :--- | :--- | :--- |
| `availableProcessors` | number of processors of the current machine | Number | *Always* |
| `loadAverage` | system load average | Number | *Always* |
| `totalSwapSpace` | total swap space, in bytes | Number | *Always* |
| `totalPhysicalMemory` | total physical memory, in bytes | Number | *Always* |
| `freePhysicalMemory` | total physical memory, in bytes | Number | *Always* |

### Description

The `top` processor generates system load information at a regular interval.
 
### Examples

#### Trigger an alert when the CPU is higher than 80%

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

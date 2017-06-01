## `pipe`

Executes processors in parallel.

### Input and output

* Category: Control
* Input: Constrained by the type of input of the processors
* Output: Output of the processors, if any

### Synopsis

```js
parallel(processor1, processor2, ...)
parallel([ processor1, processor2, ... ])
parallel(configuration_object)
```
### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `processors` | processors to be executed | list of processors | *Mandatory* | 

### Description

The `parallel` processor executes processors in parallel. The input of the `parallel` processor is sent to 
each processor, and the output of each processor is sent to the output of the `parallel` processor. 

### Examples

#### Trigger an alert if the file `application.log` contains `ERROR` or `WARNING`

```js
pipe(
  tail("application.log"), 
  parallel(grep(/ERROR/), grep(/WARNING/)), 
  alert("Error or warning")
).run();
```

#### Trigger an alert if either `application1.log` or `application2.log` contains `ERROR`

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

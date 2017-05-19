## `pipe`

Executes processors in parallel

### Input and output

* Category: Special
* Input: Same as the input of the processors
* Output: Same as the output of the processors

### Synopsis

```js
parallel(processor1, processor2, ...)
parallel([ processor1, processor2, ... ])
parallel(configuration_object)
```
### Configuration object properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `processors` | processors to be executed | list of processors | *Mandatory* | 

### Description

The `parallel` processor executes processors in parallel. The input of the `parallel` processor is sent to 
each processor, and the output of each processor is sent to the output of the `parallel` processor. 

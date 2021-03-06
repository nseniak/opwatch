## `pipe`

Chains processors.

### Input and output

* Category: Control
* Input: Sams as the first processor of the chain
* Output: Same as the last processor of the chain

### Synopsis

```js
pipe(processor1, processor2, ...)
pipe([ processor1, processor2, ... ])
pipe(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `processors` | processors to be chained | list of processors | *Mandatory* |
 
### Description

The `pipe` processor chains processors into one, piping the output of each processor to the input of the next one.
With zero processors, `pipe` lets its input through.

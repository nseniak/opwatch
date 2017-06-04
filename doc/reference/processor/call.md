## `call`

Generates output using a callback.

### Input and output

* Category: Producer
* Input: None
* Output: Callback result

### Synopsis

```js
call(lambda)
call(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `lambda` | callback that computes the processor's output | () => Object | *Mandatory* | 
| `period` | period at which the callback is called | [Duration](../programming.md#Durations) | `"1s"` |

### Description

The `call` processor invokes the `lambda` callback at a regular interval to generate output.

### Example

#### Count seconds

```js
var count = 0;

pipe(
  call(function () { return count++; }),
  stdout()
).run();
```
## `call`

Generates output using a callback.

### Input and output

* Category: Producer of Filter, depending on the configuration options
* Input: Any object or value, or None, depending on the configuration
* Output: Callback result

### Synopsis

```js
call(output)
call(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `output` | callback that computes the processor's output | () => Object | *Mandatory* | 
| `input` | callback that is applied to the processor's input | () => Void | *Optional* | 
| `delay` | initial delay after which the `output` callback is first called | [Duration](../programming.md#Durations) | `"0s"` |
| `period` | period at which the `output` callback is repeatedly called | [Duration](../programming.md#Durations) | `"10s"` |

### Description

The `call` processor invokes the `output` callback at a regular interval. If the returned value is different
from `undefined`, it is sent to the processor's output.

If the `input` callback is defined, then `call` accepts inputs and can be used as a filter. The `input` callback is 
then invoked on any received input. If `input` is not defined, then `call` does not accept any input and should
be used as a consumer. 

See also: [`apply`](apply.md).

### Example

<!-- example-begin -->
#### Count seconds

```js
var count = 0;

pipe(
  call(function () { return count++; }),
  stdout()
).run();
```
<!-- example-end -->

<!-- example-begin -->
#### Every hour, publish the number of lines containing SIGNUP that were added to a log file

```js
var count = 0;

pipe(
  tail("application.log"),
  grep(/SIGNUP/),
  call({
    input: function () { count++; },
    output: function () { 
      var result = count;
      count = 0;
      return result; 
    },
    delay: "1h",
    period: "1h"
  }),
  alert({
    title: "New lines in log file during the last hour",
    level: "low"
  })
).run();
```
<!-- example-end -->

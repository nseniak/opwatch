## `apply`

Transforms its input using a callback.

### Input and output

* Category: Filter
* Input: Any value or object
* Output: Callback result

### Synopsis

```js
apply(output)
apply(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `output` | callback that computes the processor's output | (input, payload) => Object | *Mandatory* | 

### Description

The `apply` processor invokes the `output` callback on any received input. If the returned value is different
from `undefined`, it is sent to the processor's output.

See also: [`call`](call.md).

### Example

<!-- example-begin -->
#### Trigger an alert if the average free swap space over 5 minutes is smaller than 10 megabytes

```js
pipe(
  top(),
  apply(function (topOutput) { return topOutput.freeSwapSpace; }),
  trail("5m"),
  alert({
  	title: "free swap space is low",
  	trigger: function (freeSwapSpaceTrail) { return stats(freeSwapSpaceTrail).mean < 1e7; },
  	toggle: true
  })
).run();
```
<!-- example-end -->

<!-- example-begin -->
#### Print non-empty log file lines 

```js
pipe(
  tail("application.log"),
  apply(function (line) { 
  	if (line.length !== 0) return line;
  	// Otherwise undefined is returned
  }),
  stdout()
).run();
```
<!-- example-end -->

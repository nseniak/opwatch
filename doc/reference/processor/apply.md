## `apply`

Computes its output using a callback.

### Input and output

* Category: Filter
* Input: Any
* Output: Any

### Synopsis

```js
grep(lambda)
grep(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `lambda` | callback that computes the processor's output | (input, payload) => Object | *Mandatory* | 

### Description

The `apply` processor invokes the `lambda` callback on any received input and sends the returned value to its output.

### Example

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
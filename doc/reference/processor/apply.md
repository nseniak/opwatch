## `apply`

Computes its output using a callback

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

Print the length of log file lines:

```js
pipe(
  tail("application.log"),
  apply(function (input) { return input.length; }),
  stdout()
).run();
```
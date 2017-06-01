## `test`

Callback-based filter.

### Input and output

* Category: Filter
* Input: Any value or object
* Output: The input value or object

### Synopsis

```js
test(lambda)
test(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `lambda` | predicate that determines if the input should be sent to output | (input, payload) => Boolean | *Mandatory* | 

### Description

The `test` processor invokes the `lambda` predicate on any received input. If the returned value is true, the input
is sent to the processor's output.

### Example

#### Print non-empty log file lines 

```js
pipe(
  tail("application.log"),
  test(function (line) { return line.length !== 0; }),
  stdout()
).run();
```
## `test`

Callback-based filter.

### Input and output

* Category: Filter
* Input: Any value or object
* Output: The input value or object

### Synopsis

```js
test(predicate)
test(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `predicate` | function that determines if the input should be sent to output | (input, payload) => Boolean | *Mandatory* | 

### Description

The `test` processor invokes the `predicate` function on any received input. If the returned value is true, the input
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
## `stdout`

Writes its input to the standard output or a file.

### Input and output

* Category: Consumer
* Input: Any value or object
* Output: None

### Synopsis

```js
stdout()
stdout(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `file` | file to which text is appended | String | *Optional* | 

### Description

The `stdout` processor converts its input to text using `JSON.stringify`. If `file` is not provided, this text is
written to the standard output. Otherwise, it is appended to the given file.

### Examples

<!-- example-begin -->
#### Write system load information to standard output

```js
pipe(
  top(),
  stdout()
).run();
```
<!-- example-end -->

<!-- example-begin -->
#### Write data received from another Opwatch instance to a file

```js
pipe(
  receive("data"),
  stdout({ file: "remote-data.log.json" })
).run();
```
<!-- example-end -->

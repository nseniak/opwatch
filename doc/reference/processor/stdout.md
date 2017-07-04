## `stdout`

Writes its input to the standard output.

### Input and output

* Category: Consumer
* Input: Any value or object
* Output: None

### Synopsis

```js
stdout()
```

### Description

The `stdout` processor converts its input to text using `JSON.stringify` and writes it to the standard output.

### Examples

#### Write system load information to standard output

```js
pipe(
	top(),
	stdout()
).run();
```

## `stdin`

Reads lines from standard input.

### Input and output

* Category: Producer
* Input: None
* Output: String

### Synopsis

```js
stdin()
```

### Description

The `stdin` processor reads lines from the standard input and outputs them as strings.
 
### Payload metadata

In addition to its output, `stdin` produces a metadata object that has the following properties:

| Property | Description | Type | Presence | 
| :--- | :--- | :--- | :--- |
| `line` | line number in the standard input since the processor was started (0-based) | Number | *Always* |

This object can be accessed by the `metadata` property of the [payload](processors.md) object. For example, the
following writes the lines read from the standard input, prefixed with their number:

```js
pipe(
	stdin(),
	apply(function (input, payload) { return payload.metadata.line + ":" + input; }),
	stdout()
).run();
```
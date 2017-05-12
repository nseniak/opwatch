## `grep`

Regexp-based filter

### Input and output

* Category: Filter
* Input: String
* Output: String

### Synopsis

```js
grep(regexp)
grep(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `regexp` | a regexp to match against the input string | RegExp | *Mandatory* | 
| `invert` | if true, passes *non-macthing* input | Boolean | `false` |

### Description

The `grep` processor matches its input against the given 
[RegExp](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp). If the matching 
succeeds, the input is passed to the output. Otherwise, it is ignored.

The matching is performed using the RegExp `test` method.

### Example

Signal an alert for each line in a log file that contains `error` or `warning`, ignoring case:

```js
pipe(
  tail("application.log"), 
  grep(/error|warning/i), 
  alert("Problem found!")
).run();
```

Signal an alert for each line in a log file that does *not* contain `info:`:

```js
pipe(
	tail("application.log"), 
	grep({ regexp: /info:/, invert: true }), 
	alert("Problem found!")
).run();
```

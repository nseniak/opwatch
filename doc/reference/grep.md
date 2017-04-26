## `grep`

Regexp-based filter

### Synopsis

* Category: Filter
* Input: String
* Output: String

### Description

The `grep` processor matches its input against a given 
[RegExp](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp). If the matching 
succeeds, the input is passed to the output. Otherwise, it is ignored.

The matching is performed using the RegExp `test` method.

### Parameters

| Parameter | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `regexp` | a regexp to match against the input string | RegExp | *Mandatory* | 
| `invert` | if true, passes *non-macthing* input | Boolean | `false` |

### Example

The following code signals an alert for each line in a log file that contains the `error` or `warning` keywords, 
ignoring case:

```js
pipe(tail("application.log"), grep(/error|warning/i), alert("Problem found!")).run()
```

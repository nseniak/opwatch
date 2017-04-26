## `grep`

Regexp filter.

### Synopsis

* Category: Filter
* Input: String
* Output: String

### Description

Matches its input against a RegExp. If the matching succeeds, the input is passed as the output. Otherwise, it is
ignored.

The matching is performed using the RegExp [`test` method](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/RegExp). 

### Parameters

| Parameter | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `regexp` | a regexp to match against the input string | RegExp | *Mandatory* | 
| `invert` | if true, passes *non-macthing* input | Boolean | `false` |

### Example

The following code signal alerts for lines containing the ERROR or WARNING keyword in a log file:

```js
pipe(tail("application.log"), grep(/ERROR|WARNING/), alert("Problem found!")).run()
```

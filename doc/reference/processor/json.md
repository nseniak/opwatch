## `json`

Parses a string into a Json object.

### Input and output

* Category: Filter
* Input: String
* Output: Value or object parsed from the input

### Synopsis

```js
json(json)
json(configuration_object)
```

### Configuration properties

None.

### Description

The `json` processor parses the string it receives as its input into a Json object which is sent to its output. Any parsing
error triggers an alert.

### Example

<!-- example-begin -->
#### Trigger an alert if a json object log contains an error

This example assumes that an application appends Json objects to the structured log file `application.log.json`: 

```js
pipe(
  tail("application.log.json"),
  json(),
  alert({
  	title: "error occurred in the log",
  	trigger: function (logObject) { return logObject.level === "ERROR"; }
  })
).run();
```
<!-- example-end -->

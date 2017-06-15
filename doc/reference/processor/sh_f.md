## `sh_f`

Transforms its input using a shell command.

### Input and output

* Category: Filter
* Input: Any value or object convertible to Json
* Output: String representing a line of text produced by the shell command

### Synopsis

```js
sh_f(command)
sh_f(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `command` | shell command to execute | String | *Mandatory* | 

### Description

The `sh_f` processor starts `command` in a separate process using `sh -c <command>`. Every input received by
`sh_f` is converted to a string using `JSON.stringify` and sent to this process's input. Every line produced
by the process is sent to `sh_f`'s output as a String.

### Example

#### Use the `grep` unix command to filter payload

```js
pipe(
  tail("application.log"),
  sh_f("grep --line-buffered \"pattern\""),
  alert({
  	title: "error in log",
  })
).run();
```

The `--line-buffered` option forces `grep`'s output to be line buffered. By default, `grep`'s output is 
block buffered when standard output is not a terminal, which is the case here; without this option, the output 
would be delayed until the buffer is full, which would also delay the alarm. In general, you should use
shell commands with buffered output with care, as they might not yield to the natural behavior you would expected.

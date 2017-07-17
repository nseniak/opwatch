## `sh`

Generates output by executing a shell command.

### Input and output

* Category: Consumer, Filter and Producer
* Input: Any value or object convertible to Json (optional)
* Output: String representing a line of text produced by the shell command

### Synopsis

```js
sh(command)
call(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `command` | shell command to execute | String | *Mandatory* | 
| `delay` | initial delay after which the command is first executed | [Duration](../programming.md#Durations) | `"0s"` |
| `period` | period at which the command is repeatedly executed | [Duration](../programming.md#Durations) | `"10s"` |

### Description

The `sh` processor executes `command` at a regular interval. On Linux, the command is executed using `sh -c <command>`.
On Windows, it is executed using `cmd /c <command>`.

The command's output is sent to the processor's output. Each line produced by the command is treated as a 
separate output.

Commands are executed in sequence: if a command takes longer than `delay` to execute, `sh` waits for its termination 
before executing the next command. This allows using `sh` to execute blocking shell commands that won't exit
and will generate output continuously, like `netstat`.

The `sh` processor can optionally take an input. Every input received by `sh` is converted to a string using 
`JSON.stringify` and sent to the command's input.

### Example

<!-- example-begin -->
#### Triggers an alert if the number of files in a directory is greater than 100 

Note the use of `json` to convert the string output of `sh` to a number: 

```js
pipe(
  sh("ls /tmp | wc -l"),
  json(),
  alert({
    title: "Too many files",
    trigger: function (count) { return count > 100; },
    toggle: true
  })
).run();
```
<!-- example-end -->

<!-- example-begin -->
#### Trigger an alert if there's no `mongod` running process

```js
pipe(
  sh("pgrep -q mongod && echo UP || echo DOWN"),
  alert({
    title: "mongod is down",
    trigger: function (shOutput) { return shOutput === "DOWN"; },
    toggle: true
  })
).run();
```

Note how the `sh` command is designed to generate an output both in case of success (`UP`) and in case of failure 
(`DOWN`), which allows us to use a toggle alert that will start when the `mongod` process doesn't exist and will end 
when the `mongod` process is restarted. This is to be contrasted with the less good approach below:

```js
// Not great: triggers an alert every second as long as mongod is down. 
pipe(
  sh("pgrep -q mongod || echo DOWN"),
  alert({
    title: "mongod is down",
  })
).run();
```
<!-- example-end -->

<!-- example-begin -->
#### Use the `netstat` Unix command to trigger an alert if a certain remote host connects to the current one

```js
pipe(
  sh("netstat"),
  grep("some.hostname.com.*ESTABLISHED"),
  alert({
  	title: "Suspicious connection",
  })
).run();
```
<!-- example-end -->

<!-- example-begin -->
#### Use the `grep` Unix command to filter payload

```js
pipe(
  tail("application.log"),
  sh("grep --line-buffered \"pattern\""),
  alert({
  	title: "error in log",
  })
).run();
```

The `--line-buffered` option forces `grep`'s output to be line buffered. By default, `grep`'s output is 
block buffered when standard output is not a terminal, which is the case here; without this option, the output 
would be delayed until the buffer is full, which would also delay the alarm. In general, you should use
shell commands with buffered output with care, as they might not yield to the natural behavior you would expected.
<!-- example-end -->

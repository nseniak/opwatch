## `sh`

Generates output by executing a shell command.

### Input and output

* Category: Producer
* Input: None
* Output: String

### Synopsis

```js
sh(command)
call(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `command` | shell command to execute | String | *Mandatory* | 
| `period` | period at which the callback is called | Number of milliseconds or String | `1s"` |

### Description

The `sh` processor executes `command` using `sh -c <command>` at a regular interval. The command's
output is sent to the processor's output. Each line produced by the command is considered a separate output.

### Example

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

#### Trigger an alert if there's no `mongod` running process

```js
pipe(
	sh("pgrep -q mongod && echo UP || echo DOWN"),
  alert({
  	title: "mongod is down",
  	trigger: function (input) { return input === "DOWN"; },
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

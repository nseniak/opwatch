# Opwatch

A versatile command line tool for monitoring live systems to detect problems and generate alerts.
 
## What is Opwatch?

Opwatch is a Javascript interpreter with a built-in toolbox of functions dedicated to monitoring and alerting. 
This toolbox includes Unix-like functions like `tail` and `grep`, statistical functions, and alerting functions.

Opwatch lets you perform a variety of tasks like monitoring website uptime, checking a log file for specific messages
or exceptions, monitoring disk space and CPU trends, or invoking an application's healthcheck endpoint. Alerts can be 
published on [Slack](https://slack.com/) and [Pushover](https://pushover.net/).
 
Since monitoring applications are plain Javascript programs, they can be modularized, versioned and reused across 
systems.

### What Opwatch is not
 
Opwatch does not aim at being a complete monitoring platform. It does not include a database, does not index log files, 
and does not have a graphical interface with charting. It is designed for alerting and can be used as a complement to 
other monitoring tools.

## Installation

TODO

## Quick start

### A simple Opwatch program

Here's an example of a small Opwatch program:

```js
processor = pipe(tail("application.log"), grep(/ERROR/), alert("An error occurred!"));
processor.run();
```

The first line of code builds a *processor* using the functions `pipe`, `tail`, `grep` and `alert`. 
Processors are the building blocks of monitoring programs. Like Unix commands, they take an input, perform a specific 
function, and produce an output.

The second line of code runs the processor, entering an infinite loop that only stops when the Opwatch process 
forcibly exits.

If you're familiar with Unix commands and Javascript, you can probably infer the goal of this program. The `tail`
processor is equivalent to the Unix command `tail -F -n 0`, thus starting at the bottom of the file and waiting for 
additional data to be appended. The `grep` processor takes a Javascript regexp as its argument and
behaves similarly to the Unix `grep` command, passing any matching input to the next processor.

The `alert` processor raises an alert when it receives an input. By default, alerts are printed 
to the console (a.k.a standard output), but they can use third-party messaging services like Slack. In addition to 
a title, Alerts have optional parameters including a level, trigger condition, body (i.e., extended content) and toggle 
mode.

To run this program, invoke the `opwatch` command with the program file as its argument -- let's assume it's
`my_first_processor.js`:

```sh
$ opwatch my_first_processor.js
```

After a few seconds, you get a message informing you that the processor is running. Now, every time you append a line 
containing the keyword `ERROR` to the file `application.log`, you get a message with the alert title and the content
of the matching line:

```
[console] info: processor up and running
[console] alert: An error occurred!
[console] >> This is a line containing the ERROR keyword
[console] alert: An error occurred!
[console] >> Another line containing the ERROR keyword
// And so on until stopped
```

To stop the program, type Ctrl-C or kill the Opwatch process.

You can also run this program on a single command line using the `--run` option whose argument is a
Javascript expression that evaluates to a processor:

```sh
$ opwatch --run 'pipe(tail("application.log"), grep(/ERROR/), alert("An error occurred!"))'
```

### The Opwatch shell

Opwatch can run as an interactive Javascript read-eval-print loop, which is useful for learning and experimenting. 
To start the interactive loop, run the Opwatch command without any argument:

```sh
$ opwatch
```

A few tips:

* Type Ctrl-C to interrupt a running processor. Typing Ctrl-C in the shell does not exit Opwatch like when it is running 
  a script file, but returns to the interactive loop.
* Type `help()` to list the available predefined processors. To get more detailed help on a specific processor, for 
  example `grep`, type `grep.help()`.
* Type `pretty(processor)` to pretty-print a processor.
* Type `exit()` to exit the Opwatch shell.

## Overview

The overview presents the key concepts that will help you read the reference documentation.

### Processors

Opwatch draws from the Unix toolbox philosophy. Like Unix commands, processors are components that 
consume an input, perform a function, and produce an output. Inputs and outputs can be connected using pipelines.

Many processors, like `tail` and `grep`, are named after their Unix counterpart and have a similar function. 
However, they don't necessarily have the exact same options or behavior. The goal here is to reuse good ideas and 
familiar names from Unix, not to stick to the letter.

Here are several key differences between Opwatch processors and Unix commands:

* In Unix, a command receives plain text as its input and produces plain text as its output. 
An Opwatch processor can consume and produce arbitrary Javascript objects, not only strings. For example, the `df` 
processor, which computes disk space information, produces a Javascript object with properties like `size`, 
`used` and `available` that contain number of bytes.
* Many Unix commands that generate an output are one-shot. For example, the `df` command outputs disk space 
information once and then exits. In Opwatch, processors don't exit until forcibly stopped. Processors that generate
an output, like `df`, repeat their output at a regular interval -- every second by default. This allows creating 
programs that monitor a system continuously.

For an example of how `df` behaves, run the command below. The output of `df` is pipelined to `stdout`, 
a processor that consumes any Javascript object and displays it in Json syntax, mostly for debugging purposes:

```js
> pipe(df("/tmp"), stdout()).run()
{"file":"/tmp","size":499055067136,"used":347210567680,"available":151844499456,"percentUsed":69.57359829499134}
{"file":"/tmp","size":499055067136,"used":347210567680,"available":151844499456,"percentUsed":69.57359829499134}
{"file":"/tmp","size":499055067136,"used":347210567680,"available":151844499456,"percentUsed":69.57359829499134}
// And so on until stopped
```

As a more practical example, the program below generate alerts if the disk space is used at more than 90%. The `test` 
processor applies a Javascript function to its input, and lets it pass if and only if the function returns a true 
boolean value:

```js
pipe(df("/tmp"), test(function (input) { return input.percentUsed > 90 }), alert("Not enough space left")).run()
```

### Combining processors

Processors can be combined with `pipe` and `parallel`:

* `pipe` sequentially connects the inputs and outputs of a list of processors;
* `parallel` dispatches its input to a list of processors running in parallel, and merges their output.

For example, the following processor monitors a log file and generates an alert if a line contains either ERROR
or WARNING:

```js
pipe(
  tail("application.log"), 
  parallel(grep(/ERROR/), grep(/WARNING/)), 
  alert("Error or warning")
).run()
```

Pipes and parallel processors can be arbitrarily nested. In real monitoring applications, nesting
can become quite deep, which makes them hard to write and read in one block. We recommend that you modularize 
the construction of processors to make code easier to write and read, and to yield to code reuse, like
in this example:

 
```js
function grepErrorOrWarn() {
  return parallel(grep(/ERROR/), grep(/WARNING/));
}

function checkLogFile(file) {
  return pipe(
    tail(file), 
    grepErrorOrWarn(), 
    alert("Error or warning: " + file)
  );
}

checkLogFile("application.log").run()
```

### Constructor syntax

Javascript functions like `tail` or `grep` that are used to build processors are called *constructors*. A 
constructor takes a configuration object as its argument. The configuration object has properties that specify the 
processor options. For example, the `grep` processor has two configuration properties:

* `regexp`, property whose value must be a regrexp object. This property is mandatory.
* `invert`, a boolean property that specifies if the processor should pass when the regexp is not matched, 
  instead of matched. This property is optional, and defaults to `false`.

The two following calls to the `grep` constructor are equivalent:

```js
grep({ regexp: /ERROR/, invert: false });
grep({ regexp: /ERROR/ }); // invert is false by default
```

The following call to the `grep` constructor is illegal because the mandatory `regexp` property is missing:

```js
grep({ invert: false }); // => Yields an error message
```

#### Shorthands

When a processor has exactly one mandatory property, its constructor can be invoked directly with the value of this 
property, instead of an object having that property. For instance, the following calls to the `grep` constructor 
are equivalent:

```js
grep({ regexp: /ERROR/ }); // regexp is the only mandatory property of grep
grep(/ERROR/); // directly pass the regexp value
```

The `pipe` and `parallel` constructors are a special case. They have one mandatory property: `processors`, which 
must contain an array of processors. These processors can be directly passed to constructor as so many arguments, 
without putting them in an array. Therefore the following calls are equivalent:

```js
pipe({ processors: [ df("/tmp"), stdout() ] })
pipe([ df("/tmp"), stdout() ])
pipe(df("/tmp"), stdout())
```

### Categories of processors

Processors fall into the following categories, depending on how they handle input and output:

* *Producers* generate an output without taking any input. Examples: `tail`; `df`.
* *Consumers* take an input and don't generate any output. Examples: `alert`; `stdout`.
* *Filters* take an input and generate an output. Example: `grep`.
* *Control processors* combine other processors into a new processor. Their input/output behavior depends on the 
   processors they combine. Example: `pipe`. For instance, `pipe(grep(/ERROR/), alert("problem))` is a consumer, while 
  `pipe(tail(file), grep(/ERROR/))` is a producer.

These categories are useful for documentation purposes. Opwatch also uses them to check the validity of constructed 
processors. For instance, if you try to pipeline a consumer into another consumer, you get an error message:

```js
> pipe(alert("problem"), stdout()).run()
[console] error: alert: input is missing, has no output but expected to have one
[console] >> at <eval>:1
```

Unlike Unix commands, processors don't an implicit input and output. A consumer must explicitly get its input from 
another processor, and a producer must explicitly send its output to another processor. If you try to run a 
processor that breaks these rules, you get an error message:

```js
> df("/tmp").run()
[console] error: df: output is ignored
[console] >> at <eval>:1
```

To read from the keyboard or write to the screen, use the `stdin` and the `stdout` processors as in the following 
example:

```js
> pipe(stdin(), grep('ERROR'), stdout()).run()
// Type some text with the keyword ERROR
```

### Alert channels

By default, Opwatch prints alerts on the console. However, in many monitoring applications, it's more practical to have
them displayed on third-party messaging services like Slack. Opwatch lets you do that by configuring the *channels* 
on which alerts are published.

A channel corresponds to a configuration of a messaging service with all the necessary parameters, which
depend on the type of service. For instance, a Slack channel is parameterized  with a webhook URL that you 
obtain from the Slack admin interface, and which Opwatch uses to push alert messages.

Channels are configured using the `config.channels()` function, which takes a channel descriptor as its argument. For 
example, the code below defines a channel called `developers` that is connected to Slack:

```js
config.channels({
	"services": {
		"slack": {
			"channels": {
				"developers": {
					"webhookUrl": "https://hooks.slack.com/services/thisisafakeurl"
				}
			}
		}
	},
	"applicationChannel": "developers",
	"systemChannel": "developers",
	"fallbackChannel": "console"
});
```

Once this code is executed, subsequent alerts get published on Slack. The configuration properties `applicationChannel`, 
`systemChannel` and `fallbackChannel` specify channel usage:

* `applicationChannel` is the channel used by default by `alert` processors. This default can be overridden 
in each processor, so you can send alerts to different channels.
* `systemChannel` specifies the channel used by Opwatch to signal execution errors and operation events 
like startup and shutdown. For instance, if a Javascript error occurs in an Opwatch application, you'll be notified 
by an alert through the specified system channel.
* `fallbackChannel` specifies the channel that Opwatch uses when using a primary channel fails. Failures can occur 
when a channel's service is down or when its configuration contains an error.

The `console` channel is predefined and corresponds to the standard output.

Opwatch currently implements the following types of channels: 

* The *Console* channel, which print alerts on the standard output. This is the default channel and is mostly 
  used for learning and testing;
* *[Slack](https://slack.com/)* channels, which print alerts as messages in Slack;
* *[Pushover](https://pushover.net/)* channels, which send alerts as Pushover notifications;
* *Remote* channels, which send alerts to other Opwatch servers in order to let them publish them.

#### Channel throttling

An Opwatch application can potentially publish hundreds of alerts per second. If this happens, it would completely
clog the user interface of the messaging service used to display the alert, and even consume entirely the usage quota 
of this service.

Such a high alert rate is usually not a desired behavior, but it can happen if the application monitors something
that goes wrong to an unexpected extent -- for instance, if it looks for errors in a log file and the error 
rate becomes extremely high due to some general failure. 

To handle this issue, the Slack and Pushover channels implement *throttling*. The configuration of each 
channel has a `maxPerMinute` property that limits the number of alerts that can be displayed. When this
limit is reached, you're warned by a last informational alert and then the channel goes mute. As soon as it recovers 
some capacity, you get an alert that contains a summary of the alerts that were muted.

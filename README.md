# Opwatch

## What is Opwatch?

Opwatch is a command line tool for monitoring live systems to detect problems and generate alerts. It consists in a 
Javascript interpreter with a toolbox of predefined functions dedicated to monitoring and alerting. This toolbox
includes Unix-like functions like `tail` and `grep`, statistical functions, and alerting functions. To monitor a system, 
you write a Javascript program that uses these functions to probes your system, identify issues and send alerts.

The goal of Opwatch is to let you cover all aspects of a live system and make sure nothing goes awry. Its design
goals are:

* Versatility. You can use Opwatch to monitor website uptime, check a log file for error messages or Java exceptions, 
monitor disk space, invoke an application's healthcheck endpoints. You can trigger alerts based on any condition that 
can be expressed in Javascript, using its statistical library or any other functions.
* Modularity. Because it is programmable in Javascript, Opwatch lets you to define libraries of reusable 
functions that can be shared across applications.

On the other hand, Opwatch does not aim at being a complete monitoring platform. It does not include a database, 
log indexing, search and charting. It is specifically designed for alerting and can be used as a complement to
monitoring platforms.

### A simple Opwatch program

Here's an example of a small Opwatch program `my_first_processor.js`:

```js
processor = pipe(tail("application.log"), grep(/ERROR/), alert("An error occurred!"));
processor.run();
```

The first line of code builds a *processor* using the constructors `pipe`, `tail`, `grep` and `alert`. In Opwatch,
processors are the building blocks of monitoring programs. Like Unix commands, they take an input, perform a specific 
function, and produce an output.

The second line of code runs the processor, entering an infinite loop that only stops when the Opwatch process 
forcibly exits.

If you're familiar with Unix commands and some Javascript, you can probably infer the goal of this program. The `tail`
processor is equivalent to the Unix command `tail -F -n 0`, thus starting at the bottom of the file and waiting for 
additional data to be appended. The `grep` processor takes a Javascript regexp as its argument and
behaves similarly to the Unix `grep` command, passing any matching input to the next processor.

The `alert` processor is specific to Opwatch and raises an alert with a given title. By default, alerts
are printed to the console (a.k.a standard output), but Opwatch can redirect them to other channels, 
like [Slack](https://slack.com/) or [Pushover](https://pushover.net/).

To run this program, invoke the `opwatch` command with the program file as its argument. After a few seconds, 
you get a message informing you that the processor is running. Now, every time you append a line containing the 
keyword `ERROR` to the file `application.log`, you get a message with the alert title and the matching line:

```
$ opwatch my_first_processor.js
[console] info: processor up and running
[console] alert: An error occurred!
[console] >> This is a line containing the ERROR keyword
[console] alert: An error occurred!
[console] >> Another line containing the ERROR keyword
// And so on until stopped
```

To stop the program, type Ctrl-C or kill the Opwatch process.

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

### Command line execution

The `--run` option lets you run a processor with a single command line. Its argument is a Javascript expression 
that must evaluate to a processor. For example:

```sh
$ opwatch --run 'pipe(tail("application.log"), grep(/ERROR/), alert("An error occurred!"))'
```

## Processors

Opwatch draws from the Unix toolbox philosophy. Like Unix commands, processors are components that 
consume an input, perform a function, and produce an output. Inputs and outputs can be connected using pipelines.

Many processors, like `tail` and `grep`, are named after their Unix counterparts and have a similar function. 
However, they don't necessarily have the exact same options or behavior. The goal here is to reuse good ideas and familiar
names from Unix, not to stick to the letter.

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

## Combining processors

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

## Alerts

The `alert` processor is the 

### Channels

### Gating

### Errors


### Statistics




### Categories of processors

Processors fall into the following categories, depending on how they handle input and output:

* *Producers* generate an output without taking any input. Examples: `tail`; `df`.
* *Consumers* take an input and don't generate any output. Examples: `alert`; `stdout`.
* *Filters* take an input and generate an output. Example: `grep`.
* *Control processors* combine other processors into a new processor. Their input/output behavior depends on the 
   processors they combine. Example: `pipe`. For instance, `pipe(grep(/ERROR/), alert("problem))` is a consumer, while 
  `pipe(tail(file), grep(/ERROR/))` is a producer.

These categories are useful for documentation purposes. They also allow Opwatch to check the validity of constructed 
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

## Advanced notions

### Payloads

In Unix, commands consume their input and produce their output as plain text. In Opwatch, processors consume
and produce *payload* objects, which contain contextual information that is useful when signaling problems and 
tracing them to their root cause.

A payload has the following properties:

* `value`: the value carried by the payload. The type of this value depends on the processor that produced it.
  For instance, the `tail` processor produces payloads whose value is a string -- the line from the tailed file.
* `timestamp`: an integer representing the time at which the payload was produced, as the difference, measured in 
  milliseconds, between the current that time and midnight, January 1, 1970 UTC.
* `hostname`: the hostname of the Opwatch process that produced the payload. This is useful when using Opwatch in distributed mode.
* `port`: the http port of the Opwatch process that produced the payload. This is useful when using Opwatch in distributed mode.
* `producer`: the name of the processor that produced this payload, e.g. `"tail"`.
* `previous` (optional): the payload that was taken as an input by the producer, if any.

### Distribution


Opwatch is a shell using interactive Javascript designed for monitoring 


Design goals:
* Easy to deploy and to use
* Flexible
* Lightweight and standalone; no dependency on database or other logging systems
* Designed as a toolset
* Distributed
* Extensible; ability to define reusable libraries
* Can be used as a complement to more sophisticated systems
* Versatile
Non design goals:
* Not to replace sophisticated monitoring systems

p = pipe(top(), stdout())
run(p)

Here,
top is a producer. It takes no input and generates an output.
stdout is a consumer. It takes an input and generates no output.

Here's another example:

run(pipe(tail("/tmp/foo.log"), grep(/ERROR/), stdout()))

grep is a filter

Most useful purpose is to raise alarms

run(pipe(tail("/tmp/foo.log"), grep(/ERROR/), alarm("error in the log!)))

help()
grep.help()

alerter -e "pipe(tail('/tmp/foo.log'), grep(/ERROR/), alert('Error found))"

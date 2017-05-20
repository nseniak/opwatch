# Opwatch

A versatile command line tool for monitoring live systems to detect problems and generate alerts.
 
## What is Opwatch?

Opwatch is a Javascript interpreter with a built-in toolbox of functions dedicated to monitoring and alerting. 
This toolbox includes Unix-like functions like `tail` and `grep`, statistical functions, and alerting functions.

Opwatch lets you perform a variety of tasks like monitoring website uptime, checking a log file for specific messages
or exceptions, monitoring disk space and CPU trends, or invoking an application's healthcheck endpoint. Alerts can be 
published on [Slack](https://slack.com/) and [Pushover](https://pushover.net/).
 
Since Opwatch monitoring applications are plain Javascript programs, they can be modularized, versioned and reused 
across systems.

### What Opwatch is not
 
Opwatch does not aim at being a complete monitoring platform. It does not include a database, does not index log files, 
and does not have a graphical interface with charting. It is designed for alerting and can be used as a complement to 
other monitoring tools.

### A taste of Opwatch

Here's an example of a small Opwatch program:

```js
var processor = pipe(tail("application.log"), grep(/ERROR/), alert("An error occurred!"));
processor.run();
```

To try it, start the `opwatch` Javascript shell and type the program:

```js
$ opwatch
> processor = pipe(tail("application.log"), grep(/ERROR/), alert("An error occurred!"));
[object pipe]
> processor.run();
// Now try and append some lines to application.log
```

The [Quick start](doc/quickstart.md) guide takes you through this example in a little more details.

## Installation

TODO

## Resources

* [Quick start](doc/quickstart.md)
* [Overview](doc/overview.md)
* [Code snippets](snippets.md)
* [Reference manual](doc/reference/reference.md)
  * [Command line arguments and options](doc/reference/command.md)
  * [Javascript reference](doc/reference/javascript.md)
  * [Processors](doc/reference/processor/processors.md)

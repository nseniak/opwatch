# Quick start

## What is Opwatch for?

Opwatch helps you dynamically monitor your server applications to detect problems and generate alerts via
channels such as [Slack](https://slack.com/) and [Pushover](https://pushover.net/). Opwatch lets you perform a variety 
of tasks like monitoring website uptime, checking a log file for specific messages or exceptions, monitoring disk 
space and CPU trends, or invoking an application's healthcheck endpoint.

## How does it work?

Opwatch consists in a Javascript interpreter with a built-in toolbox of functions dedicated to monitoring and alerting. 
This toolbox includes Unix-like functions like `tail` and `grep`, statistical functions, and alerting functions.

Since Opwatch monitoring applications are plain Javascript programs, they can be modularized, versioned and reused 
across systems.

## A simple Opwatch program

Here's an example of a small Opwatch program:

```js
var processor = pipe(tail("application.log"), grep(/ERROR/), alert("An error occurred!"));
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
on the console (a.k.a standard output), but Opwatch can also publish them on third-party messaging services like Slack.
In addition to the title, Alerts have optional parameters including a level, trigger condition, body (i.e., extended 
content) and toggle mode.

## Running the program

To run a program, invoke the `opwatch` command with the file path or URL of the program. Assuming the program above
is contained in the file `my_first_processor.js`, you execute it by typing: 

```sh
$ opwatch my_first_processor.js
```

Opwatch displays a message informing you that the processor is running. Now, every time you append a line 
containing the keyword `ERROR` to the file `application.log`, you get a message with the alert title and level
(`medium` is the default) followed by the content of the matching line:

```
[console] info: processor up and running
[console] Alert(medium): An error occurred!
[console] >> This is a line containing the ERROR keyword
[console] Info: processor up and running
[console] Alert(medium): An error occurred!
[console] >> Another line containing the ERROR keyword
// And so on until stopped
```

To stop the program, type Ctrl-C or kill the Opwatch process.

You can also run this program on a single command line using the `--run` option whose argument is a
Javascript expression that evaluates to a processor:

```sh
$ opwatch --run 'pipe(tail("application.log"), grep(/ERROR/), alert("An error occurred!"))'
```

## The Opwatch shell

Opwatch can run as an interactive Javascript read-eval-print loop, which is useful for learning and experimenting. 
To start the interactive loop, run the Opwatch command without any arguments:

```sh
$ opwatch
```

In the shell, type Ctrl-C to interrupt a running processor. This returns you to the interactive loop. To exit
the shell, type `exit()`.

## Getting help

To get help on the `opwatch` command arguments and options, type:

```sh
$ opwatch --help
```

In the read-eval-print loop, the `help()` function lists all the available predefined processors. To get more 
detailed help on a specific processor, for example `grep`, type `grep.help()`:

## What to read next

To learn more about Opwatch, we recommend that you read the [Overview](overview.md).
 
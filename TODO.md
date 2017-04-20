* [x] Make it possible to use a wrapper function for a processor definition
* Find name
* Getting started
* Reference documentation generation
* [x] Remove "js" prefix of processor names -- should be implicit
* [x] Make grep use Javascript regexes
* [x] Generify alert channels (email, other alerters, etc)
* Make JS initialization external files instead of resources, so they can be hacked by the user
* Extension mechanism for adding processors in Java, with a corresponding NPM module
* Tests
* Better handling of internal errors: limit of system alarm per processor? deactivate processor?
* Way to list the alarms currently toggled on
* Link in alarms to more info
* Persistence?
* Packaging with apt-get and/or others
* [x] Notifications rather than alarm? => no
* [x] System alerts are displayed as errors in interactive mode
* Faster console startup
* Centralized UI for muting alarms/listing current alarms/inspecting alarm details
* Javascript port
* [x] System alarms
* Static verification
* [x] Fix "pretty"
* Automatically load processors from Github
* ready() method
* Integrations: see list in https://www.statuscake.com/kb/knowledge-base/getting-alerts-from-an-integration/ and https://www.loggly.com/docs/alert-endpoints/
* mute/downgrade alarms by UI
* display alias name instead of processor as source
* display root payload of alerts
* Move to new pushover4j version
* [x] manage frequency limitation by title, not by processor
* [x] Better slack messages
* Hook to create custom Pushover and Slack messages
* [x] Aggregate messages
* [x] Fix Ctrl-C
* [x] Fix pretty
* [x] Support Ctrl-C in alerter <<EOF foo EOF
* [x] Option --run
* [x] Add processor id to payload

------

## What is Opwatch?

Opwatch is a command line tool for monitoring live systems and generating alerts. It consists in a Javascript 
shell with Unix-like functions -- tail, grep, df, pipe, etc -- plus statistical and alerting functions. These functions 
are combined to build monitoring applications.

### A simple Opwatch program

Here's an example of a small Opwatch monitoring application `my_first_processor.js`:

```
processor = pipe(tail("/tmp/application.log"), grep(/ERROR/), alert("An error occurred!"));
processor.run();
```

The first line of code builds a *processor* object using constructor functions `pipe`, `tail`, `grep` and `alert`. Processors 
are the building blocks of monitoring programs. The second line runs the processor, entering an infinite 
loop that only stops when the Opwatch process forcibly exits. If you're familiar with Unix commands 
and some Javascript, you can probably infer the goal of this program. Note that the `tail` processor has the same blocking
behaviour as the Unix command `tail -F -n 0`, thus starting at the bottom of the file and waiting for additional 
data to be appended.

To run this program, type at the command line:

```
$ opwatch my_first_processor.js
```

After a few seconds, you get a message informing you that a processor is running:
 
```
[console] info: processor up and running
```

Now, every time you append a line containing the keyword `ERROR` to the file `/tmp/application.log`, you get a message:
 
```
[console] alert: An error occurred!
```

To stop the program, type Ctrl-C or kill the Opwatch process.

### The Opwatch shell

Opwatch can also run as an interactive Javascript read-eval-print loop, which is useful for learning and experimenting. 
To start the interactive loop, run the Opwatch command without a script file argument:

```
$ opwatch
```

A few tips:

* Type Ctrl-C to interrupt a running processor. In the interactive shell, Ctrl-C does not exit Opwatch, like when
  it's invoked to execute a script, but returns to the interactive loop.
* Type `help()` to list the available processors. To get more detailed help on a specific processor, for example 
  `grep`, type `grep.help()`.
* Type `pretty(processor)` to pretty-print a processor.
* Type `exit()` to exit the Opwatch shell.

### Command line execution

The `--run` option lets you build and run a processor using a single command line. Its argument is a Javascript expression 
that must evaluate to a processor. For example:

```
$ opwatch --run 'pipe(tail("/tmp/application.log"), grep(/ERROR/), alert("An error occurred!"))'
```

## Processors

Opwatch processors are inspired from the Unix toolbox philosophy. Like Unix commands, processors are components that 
consume an input, perform a function, and produce an output. Inputs and outputs can be connected using pipelines.

Many processors, like `tail` and `grep`, are named after their Unix counterparts and have a similar function. 
However, they are not identical. They don't necessarily have the same options or behavior. The goal here is to reuse 
good ideas and familiar names from Unix, but take liberties with the original when it makes sense in the context of a 
monitoring application.

### Input and output

In Unix, commands receive plain text as their input and produce plain text as their output. 
In Opwatch, processors can consume and produce arbitrary Javascript objects, not only strings.
This facilitates the communication of structured information between processors. For example, the `df` processor, 
which fetches disk space information for a given volume, produces a Javascript object that has properties including 
the total volume size and available size.

To see an example of `df` output, run the command below. The output of `df` is pipelined to the input of `stdout`, 
which consumes any Javascript object and displays it in Json syntax, which is useful for testing and debugging purposes:

```
> pipe(df("/tmp"), stdout()).run()
{"file":"/tmp","size":499055067136,"used":347210567680,"available":151844499456,"percentUsed":69.57359829499134}
{"file":"/tmp","size":499055067136,"used":347210567680,"available":151844499456,"percentUsed":69.57359829499134}
{"file":"/tmp","size":499055067136,"used":347210567680,"available":151844499456,"percentUsed":69.57359829499134}
// And so on until stopped
```

Note that `df` doesn't exit after executing once, like its Unix counterpart, but rather repeats 
its output every second until forcibly stopped. This is a common pattern in Opwatch where the goal is to
create monitoring applications, rather than one-shot executions.

### Pipelines and parallel execution

Processors can be combined with `pipe` and `parallel`:

* `pipe` connects sequentially connects the inputs and outputs of a list of processors;
* `parallel` dispatches its input to a list of processors running in parallel and merges their output.

For example, the following processor monitors a log file and generates an alert if a line contains either ERROR
or WARNING:

```
pipe(
  tail("application.log"), 
  parallel(grep(/ERROR/), grep(/WARNING/)), 
  alert("Error or warning")
)
```

Pipes and parallel processors can be arbitrarily nested. The following processor uses `count`,
which counts its inputs over a given period of time, and `test`, which outputs its input 
if it obeys to a certain Javascript condition, to generate an alert for a log file if a line
contains ERROR or if there are more than 100 lines generated over a period of 10 seconds:

```
pipe(
  tail("application.log"), 
  parallel(grep(/ERROR/), pipe(count("1m"), test(function (c) { return c > 1000 })), 
  alert("Problem!")
)
```

### Constructors

Processors are built using constructors. A constructor takes a configuration object as its argument. The
configuration object has properties that specify the processor options. Configurations properties can 
be mandatory or optional.

For example, the `grep` processor has two configuration properties:

* `regexp`, a mandatory property whose value is a regrexp object;
* `invert`, an optional boolean property defaulting to `false` that specifies if the processor should pass 
  when the regexp is not matched, instead of matched.

The two following calls to the `grep` constructor are equivalent:

```
grep({ regexp: /ERROR/, invert: false});
grep({ regexp: /ERROR/ }); // invert is false by default
```

#### Constructor shorthands

Systematically passing full objects to constructors can create syntactic cluttering and reduce program readability. 
Opwatch provides a few syntactic shorthands for sake of brevity.

When a processor has exactly one mandatory property, its constructor can be invoked directly with the value of this 
property, instead of an object having that property. For instance, the following calls to the `grep` constructor 
are equivalent:

```
grep({ regexp: /ERROR/ }); // regexp is the onlye mandatory property of grep
grep(/ERROR/); // directly pass the regexp value
```

The `pipe` and `parallel` constructors are a special case. They have one mandatory property: `processors`, which 
contains an array of processors. These processors can be directly passed to constructor as its arguments. The following 
calls are equivalent:

```
pipe({ processors: [ df("/tmp"), stdout() ] })
pipe([ df("/tmp"), stdout() ])
pipe(df("/tmp"), stdout())
```

### Processor categories

Processors fall into the following categories, depending on how they handle input and output:

* *Producers* generate an output without taking any input. Examples: `tail`; `df`.
* *Consumers* take an input and don't generate any output. Examples: `alert`; `stdout`.
* *Filters* take an input and generate an output. Example: `grep`.
* *Control processors* combine other processors into a new processor. Their input/output behavior depends on the 
   processors they combine. Example: `pipe`. For instance, `pipe(grep(/ERROR/), alert("problem))` is a consumer, while 
  `pipe(tail(file), grep(/ERROR/))` is a producer.

These categories are useful for documentation purposes. They also allow Opwatch to check the validity of constructed 
processors. For instance, if you try to pipeline a consumer into another consumer, you get an error message:

```
> pipe(alert("problem"), stdout()).run()
[console] error: alert: input is missing, has no output but expected to have one
[console] >> at <eval>:1
```

Unlike Unix commands, processors don't an implicit input and output. A consumer must explicitly get its input from 
another processor, and a producer must explicitly send its output to another processor. If you try to run a 
processor that breaks these rules, you get an error message:

```
> df("/tmp").run()
[console] error: df: output is ignored
[console] >> at <eval>:1
```

To read from the keyboard or write to the screen, use the `stdin` and the `stdout` processors as in the following 
example:

```
> pipe(stdin(), grep('ERROR'), stdout()).run()
// Type some text with the keyword ERROR
```

### Alerts

Now we're getting to the 

The whole point of Opwatch is to generate 

The pipeline operator
`|` connects the text output of a command to the text input of the next command.

Opwatch uses the same pipeline principle to connect processors. For instance, in `my_first_processor.js`, the
`tail`, `grep` and `alert` processors are connected in a pipeline: when `tail` generates a line of text, it is
consumed by `grep`, whose output is then consumed by `alert`. 
 


### Reuse



```
function logErrorAlert(filename) {
   return tail(filename), grep(/ERROR/), alert("An error occurred in " + filename);
}

logErrorAlert("/tmp/application.log").run();
```

alias


### Alerts

Where do alerts go?

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

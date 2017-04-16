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
* Fix Ctrl-C
* Fix pretty

------

## What is Opwatch?

Opwatch is a command line tool for monitoring live systems and generating alerts. It comes as a Javascript shell 
extended with Unix-like functions -- pipe, tail, grep, df, etc -- plus statistical and alerting functions 
that, combined altogether, are used to build monitoring applications.

### Running a simple Opwatch program

Here's an example of a small Opwatch monitoring application `my_first_processor.js`:
 
```
processor = pipe(tail("/tmp/application.log"), grep(/ERROR/), alert("An error occurred!"));
run(processor);
```

The first line of code builds an Opwatch *processor*. Processors are the core components of Opwatch. They serve as 
building blocks for monitoring programs. The second line runs the processor, entering an infinite monitoring loop 
that is only interrupted when the Opwatch process forcibly exits. If you're familiar with Unix commands and some 
Javascript, you can probably infer the goal of this program.

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

To stop the program, type Ctrl-C or kill the process using the `kill <process>` command.

Alternatively, your could have run the same program with a single command line:

```
$ opwatch --run 'pipe(tail("/tmp/application.log"), grep(/ERROR/), alert("An error occurred!"))'
```

### The Opwatch shell

Opwatch can also run as an interactive shell, which is useful for learning and experimenting. Start the Opwatch command 
without a script file argument:

```
$ opwatch
```

You are then presented with a Javascript read-eval-print loop. A few tips:

* Type Ctrl-C to interrupt a running processor. In the interactive shell, Ctrl-C does not exit Opwatch, but returns 
  to the interactive loop.
* Type `help()` to list the available processors. To get more detailed help on a specific processor, for example 
  `grep`, type `grep.help()`.
* Type `pretty(processor)` to pretty-print a processor. This shows the processor in its expanded form,
  notably including its implicit configuration parameters with their default values.

### Processors

Many processors, like `tail` and `grep`, are named after Unix commands and have a similar function. 
However, they are not identical. They don't necessarily have the same options or behavior. The goal 
is to reuse good ideas and familiar names from Unix, but to take liberties with the original when it makes sense
in the framework of a monitoring application. For instance, the `tail` processor as used in
`my_first_processor.js` actually behaves like the Unix command `tail -F -n 0`.

Processors are built using constructor functions that take a configuration object as their argument. The
configuration object has properties corresponding to the processor options. Some of these properties are mandatory,
and the others are optional and can bear a default value. When a processor has exactly one mandatory property,
the value of this property can be directly passed to the constructor, instead of a configuration object. 

For example, the `grep` processor has two configuration properties: `regexp`, a mandatory property whose value is 
a Javascript regrexp object, and `invert`, an optional boolean defaulting to `false` that specifies if the processor 
should pass when the regexp is matched or when it is not. The three invocation are equivalent:

### Payload


### Alerts

Where do alerts go?

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

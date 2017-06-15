# Programming reference

## Javascript implementation

Opwatch is based on [Nashorn](https://wiki.openjdk.java.net/display/Nashorn/Nashorn+Documentation/), the Javascript
implementation that is part of Java 8. Nashorn is compliant with [ECMAScript 5.1](https://www.ecma-international.org/ecma-262/5.1/).

Nashorn provides several utility built-in functions and global objects, including:

* `$ENV` to access environment variables, e.g. `$ENV.USER`;
* `load()` to load a script (see also [Module system](#module-system));
* `print()` to print data on the console;
* `quit()` to exit the Opwatch shell

For a complete list of Nashorn built-ins, see
[Nashorn and Shell Scripting](https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/shell.html).

## Processors

For a general presentation of processors, see the [Overview](../../overview.md).

For a list of all available processors, see [Processor index](processor/processors.md).

This section contains complementary information not provided in these documents.

### Opwatch object and function types

This section describes types that are used in configuration properties or as processor input and output. 

#### Callbacks

Some processors take a Javascript function (a.k.a callback) as a configuration property. This callback is
invoked during the processor execution to yield a value or check a condition. A typical example is the [`apply`](processor/apply.md) 
processor, which applies a given callback to any input it receives, and outputs the value returned by the callback. 
For example, the following code prints the length of any line of text you type at the keyboard:

```js
pipe(
	stdin(), 
	apply(function (input) { return input.length; }), 
	stdout()
).run();
```

Another example is the [`repeat`](processor/repeat.md) processor which invokes a callback at a regular interval (10 seconds by default),
without any arguments, and outputs the returned value. For example, the following code prints the sequence of numbers
starting from 0:

```js
pipe(
  counter(),
  repeat(function() { return i++; }),
  stdout()
).run();
```

#### Payload

Callbacks that are applied to the processor's input, like in [`apply`](processor/apply.md), are also passed a second argument, called
the *payload*. The payload is an object with extended information (or metadata) about the input. It has the following 
properties:

* `value`: the input value, same as passed to the callback as its first argument.
* `timestamp`: an integer representing the time at which the value was produced by the previous processor in the pipeline.
  This time is expressed as the difference, measured in milliseconds, between the current that time and midnight, 
  January 1, 1970 UTC.
* `hostname`: the hostname of the Opwatch process that produced the value.
* `port`: the http port of the Opwatch process that produced the value.
* `producer`: the name of the processor that produced this value, e.g. `tail`.
* `previous`: the input payload of the previous processor in the pipeline, or `null` if there was no previous
  processor, i.e. in the current processor is a producer.
* `id`: an internal id used to identify the previous processor in the pipeline, or `null` if there was none.

The payload argument can be safely ignored by a callback, like in the `apply` example above where the callback only 
has a single `input` argument and the payload is silently discarded. On the other hand, to access the payload, 
just add a second argument, like in the example below which prints the lines that are typed at the keyboard, 
preceded by `from stdin:`.  

```js
pipe(
  stdin(), 
  apply(function (input, payload) { return "from " + payload.producer + ": " + input; }), 
  stdout()
).run();
```

#### Durations

Some processors have configuration properties that are expressed as *duration*. For example, the [`curl`](processor/curl.md)
processor has a `timeout` configuration property whose value is a duration.

Durations can be expressed as either a number of milliseconds, or a String representing an
[ISO-8601 duration](https://en.wikipedia.org/wiki/ISO_8601#Durations).

Example of value durations include:

* 5000: 5 seconds
* `"30s"`: 30 seconds
* `"1m"`: one minute
* `"2m30s"`: two minutes and 30 seconds
* `"2h"`: two hours 

#### SeriesObject

Processors that collect their inputs, like [`trail`](processor/trail.md) and [`collect`](processor/collect.md),
generate an array of SeriesObject objects as their outputs.

Each SeriesObject object has the following fields:
 
| Property | Description | Type | Presence |
| :--- | :--- | :--- | :--- |
| `value` | the value of the collected input | Object | *Always* |
| `timestamp` | time at which the input was received | Number | *Always* |

### The single reference rule

A processor can only be used in one place. Once a processor is used in a `pipe` or a `parallel`, it cannot be 
used in any other construct. For instance, the code below triggers an error:

```js
a = alert("Alert!");
parallel(
	pipe(tail("file1.log"), a),
	pipe(tail("file2.log"), a)
)
// => error: alert: a processor can only be used once; this one is already used in pipe
```

The common way of reusing a processor definition is to encapsulate it in a function:

```js
function myAlert() {
	return alert("Alert!");
}

parallel(
	pipe(tail("file1.log"), myAlert()),
	pipe(tail("file2.log"), myAlert())
)
// => Correct!
```

## Module system

Opwatch includes a module system based on the [npm module specification](https://nodejs.org/api/modules.html#modules_modules).
This module system uses the implementation provided by [jvm-npm](https://github.com/nodyn/jvm-npm).

## Opwatch built-in functions

### Function: `stats(data)`

This function allows performing common statistic functions on arrays produced by the 
[`trail`](processor/trail.md) and [`collect`](processor/collect.md) processors, when their inputs are numbers, i.e.
when the `value` properties of the SeriesObject objects are all numbers.

#### Arguments

| Argument | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `data` | data on which statistics are computed | array of [SeriesObject](#seriesobject) objects | *Mandatory* |
 
The `value` property of the SeriesObject objects must all be a numbers, otherwise an error is signaled.

#### Returned value

This function returns a Stats object with the following fields:

| Property | Description | Type | Presence |
| :--- | :--- | :--- | :--- |
| `mean` | the mean of the array values | Number | *Always* |
| `variance` | the variance of the array values | Number | *Always* |
| `standardDeviation` | the standard deviation of the array values | Number | *Always* |
| `max` | the maximum of the array values | Number | *Always* |
| `min` | the minimum of the array values | Number | *Always* |
| `N` | the number of array values | Number | *Always* |
| `sum` | the sum of the array values | Number | *Always* |
| `slope` | the slope of the estimated regression line | Number | *Always* |

### Function: `pretty(data)`

This function pretty-prints the passed data. It is notably useful for pretty-printing processors. 

#### Arguments

| Argument | Description | Type | Default |
| :--- | :--- | :--- |
| `data` | any data | *Mandatory* |

### Function: `help()`

This function prints information about the built-in processors.
 
### Method: `<constructor>.help()`

This method of all processor constructors (e.g., `pipe` or `tail`) prints information about the processor
configuration arguments.


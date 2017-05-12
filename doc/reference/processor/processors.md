# Processors

We assume you've already read the [Overview](../../overview.md) which contains important information about processors.
The manual covers mostly contains information that's not covered in the overview.

## Processor referencing

A processor can only be used in one place. Once a processor is used in a `pipe` or `parallel`, it cannot be 
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

### Callbacks

Some processors take a Javascript function (a.k.a callback) as a configuration property. This callback is
invoked during the processor execution to yield a value or check a condition. A typical example is the `apply` 
processor, which applies a given callback to any input it receives, and outputs the value returned by the callback. 
For example, the following code prints the length of any line of text you type at the keyboard:

```js
pipe(
	stdin(), 
	apply(function (input) { return input.length; }), 
	stdout()
).run();
```
Another example is the `repeat` processor which invokes a callback at a regular interval (1 second by default),
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

Callbacks that are applied to the processor's input, like in `apply`, are also passed a second argument, called
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


## List of processors

This section lists processors by category.

### Producers

### Filters

* [`alert`](alert.md) -- triggers an alert
* [`grep`](grep.md) -- regexp-based filter

### Consumers

### Control processors


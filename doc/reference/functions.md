# Global functions

### `pretty`

### `help`

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

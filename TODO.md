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
* Error message throttling
* Rename alerter to opwatch all over the place
* Per alert throttling

------

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

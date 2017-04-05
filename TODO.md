* Make it possible to use a wrapper function for a processor definition
* Find name
* Getting started
* Reference documentation generation
* [x] Remove "js" prefix of processor names -- should be implicit
* [x] Make grep use Javascript regexes
* Generify alert channels (email, other alerters, etc)
* Make JS initialization external files instead of resources, so they can be hacked by the user
* Extension mechanism for adding processors in Java, with a corresponding NPM module
* Tests
* Better handling of internal errors: limit of system alarm per processor? deactivate processor?
* Way to list the alarms currently toggled on
* Link in alarms to more info
* Persistence?
* Packaging with apt-get and/or others
* Notifications rather than alarm?
* System alerts are displayed as errors in interactive mode
* Faster console startup
* Centralized UI for muting alarms/listing current alarms/inspecting alarm details
* Javascript port
* System alarms
* Static verification
* Fix "pretty"
* Automatically load processors from Github
* ready() method
* Integrations: see list in https://www.statuscake.com/kb/knowledge-base/getting-alerts-from-an-integration/
* mute/downgrade alarms by UI
* display alias name instead of processor as source
* display root payload of alerts

------

Design goals:
* Easy to deploy and to use
* Lightweight and standalone; no dependency on database or other logging systems
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

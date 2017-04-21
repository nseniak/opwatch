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


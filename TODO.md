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

------

Design goals
* Not replace sophisticated monitoring systems
* Easy to deploy and to use
* Versatile
* Few dependencies with databases
* State not saved in database

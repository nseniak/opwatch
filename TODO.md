* Getting started
* Reference documentation generation
* Remove "js" prefix of processor names -- should be implicit
* Generify alert channels (email, other alerters, etc)
* Make JS initialization external files instead of resources, so they can be hacked by the user
* Extension mechanism for adding processors in Java

jscron => repeat

make processors non-repeating except repeat
repeat({processor:df(), period:"1s"})
repeat(producer)

producer
consumer
transformer

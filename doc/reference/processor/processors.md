# Processor index

### Producers

* [`curl`](curl.md) -- executes an URL request
* [`df`](df.md) -- outputs free disk space info
* [`sh`](sh.md) -- generates output by executing a shell command
* [`receive`](receive.md) -- receives values and objects from a remote Opwatch instance.
* [`stat`](stat.md) -- outputs file information
* [`stdin`](stdin.md) -- reads lines from standard input
* [`tail`](tail.md) -- outputs lines added to a file
* [`top`](top.md) -- outputs system load information

### Filters

* [`apply`](apply.md) -- transforms its input using a callback
* [`collect`](collect.md) -- collects the last `count` received inputs
* [`grep`](grep.md) -- regexp-based filter
* [`json`](json.md) -- parses a string into a Json object
* [`jstack`](jstack.md) -- parses java Java exception stack
* [`trail`](trail.md) -- collects the inputs received during the last `duration` milliseconds

### Consumers

* [`alert`](alert.md) -- triggers an alert
* [`log`](log.md) -- writes its input to a log file
* [`send`](send.md) -- sends its input to a remote Opwatch instance
* [`stdout`](stdout.md) -- writes its input to the standard output

### Producer/Filters

* [`call`](call.md) -- generates output using a callback

### Control

* [`parallel`](parallel.md) -- executes processors in parallel
* [`pipe`](pipe.md) -- chains processors

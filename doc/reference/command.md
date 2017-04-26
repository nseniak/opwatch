NAME

`opwatch` -- monitors a live system to detect problems and generate alerts.

SYNOPSIS

```sh
$ opwatch [ __options__ ] __file_or_url__...
```

```sh
$ opwatch [ __options__ ]
```

DESCRIPTION

In its first form, `opwatch` executes the given Javascript programs in sequence. In its second form, `opwatch`
starts an interactive Javascript read-eval-print loop.

At startup, `opwatch` loads the file `startup.js` from its installation directory. This file notably contains the
default channel configuration code.

The `opwatch` command starts an http server on the 28018 port. If this port is already used, the execution fails. 

OPTIONS

The following options are available:

| Option | Description |                                               
| :--- | :--- |                                               
| --help | Print command help |                                           
| --hostname __hostname__ | Specify the current machine's hostname |                    
| --init file_or_url__ | Execute this script at startup instead of the default |         
| --no-init | Do not execute any initialization script at startup |       
| --no-server | Do not start the embedded http server |                     
| --port __port_number__ | Use the specified port for the embedded http server |       
| --run __javascript_expression__ | Evaluate the given Javascript expression to a processor, and run it |                                              
| --trace-channels | Print all alerts to the standard output, instead of sending them to the messaging services |

## `receive`

Receives values and objects from a remote Opwatch instance.

### Input and output

* Category: Producer
* Input: None
* Output: Received value or object

### Synopsis

```js
receive(path)
receive(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `path` | path identifying the receiver | String | *Mandatory* | 

### Description

The `receive` processor receives values and objects sent by a `send` processor, and generates them to its outputs.

For usage examples, see [send](send.md).

### Posting objects to a `receive` processor

The `receive` processor is triggered by HTTP POST requests with Json content make to the URL 
`http://<hostname>:<port>/receive/<path>`, where `<hostname>` is the current hostname and `<port>` is Opwatch's 
embedded HTTP server port (28018 by default). For instance, the processor `receive("mypath")` is triggered
by the following command:

```bash
$ curl -H "content-type: application/json" -post 'http://localhost:28019/receive/mypath' -d '{ "field": "value" }'
```

This allows you to post any Json data to an Opwatch processor from any program.
 
## `curl`

Executes an URL request.

### Input and output

* Category: Producer
* Input: None
* Output: Response object

### Synopsis

```js
curl(url)
curl(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `url` | URL to request | String | *Mandatory* |
| `method` | HTTP method | String | `GET` |
| `headers` | extra headers to include in the request | Headers object (see below) | *Optional* |
| `data` | data to include in  the request, typically for a POST request | Any value or object | *Optional* |
| `followRedirects` | in case of a 3XX return status, redo the requesy with the new location | Boolean | `true` |
| `maxRedirects` | maximum number of redirects | Number | `50` |
| `insecure` | in case of an SSL connection, proceed even for connections otherwise considered insecure | Boolean | `false` |
| `timeout` | maximum time allowed for reading the result | [Duration](../programming.md#Durations) | `"10s"` |
| `connectTimeout` | maximum time allowed for the connection | [Duration](../programming.md#Durations) | `"5s"` |
| `delay` | initial delay after which `url` is first requested | [Duration](../programming.md#Durations) | `"0s"` |
| `period` | period at which the `url` is repeatedly requested | [Duration](../programming.md#Durations) | `"10s"` |
 
 ### Response object
 
 The Response object has the following properties:
 
| Property | Description | Type | Presence | 
| :--- | :--- | :--- | :--- |
| `status` | response status, or -1 if the request failed | Number | *Always* |
| `statusDescription` | response status description, if `status` != -1  | String | *Optional* |
| `error` | when the request failed (i.e., `status` == -1), contains an error message | String | *Optional* | 
| `url` | original request URL | String |  *Always* |
| `redirectUrl` | if redirects occurred, contains the final request URL | String | *Optional* | 
| `headers` | response headers | Headers object (see below) | *Optional* |
| `text` | if the response content type is `text/*`, the text of the response | String | *Optional* |
| `json` | if the response content type is `application/json`, the object parsed from the response | Value or Object | *Optional* |

### Header object

The Headers object represents a header list used in a request or in a response. Each property of this object corresponds 
to a header field.

If a field appears only once in the header list, its value in the Headers object is a String. If the field is repeated, 
the value is an array of Strings representing the header's multiple values.

### Description

The `curl` processor executes an URL request at a regular interval and outputs a Response object.

When the `data` property is provided, it is used as the request's body, according to the following rules:

* If no content type is specified in `headers`, the content type is set to `application/json` and
  `data` is converted to Json;
* Otherwise, if the content type and includes `application/json`, `data` is converted to Json;
* Otherwise, `data` is converted to text using `toString()`.

Request responses might arrive in a different order than the execution order. This happens when
a request is still pending when the following request is executed, and the latter request finishes earlier. 

### Examples

<!-- example-begin -->
#### Trigger an alert every 10 seconds if a Web site is down

```js
pipe(
  curl("https://httpbin.org"), 
  apply(function (curlOutput) { if (curlOutput.status != 200) return curlOutput; }), 
  alert("Website is down")
).run();
```
<!-- example-end -->

<!-- example-begin -->
#### Trigger an alert if a Web site is found to be down for at least 2 of the 10 last requests 

```js
pipe(
  curl("https://httpbin.org"), 
  collect(10), 
  alert({
    title: "Website is wobbly or down",
    trigger: function (curlOutputArray) {
      var down = 0;
      for (var i = 0; i < curlOutputArray.length; i++) {
        if (curlOutputArray[i].status != 200) down++;
      }
      return down >= 2;
    },
    toggle: true
  })
).run();
```
<!-- example-end -->

<!-- example-begin -->
#### Trigger an alert if a REST API returns a wrong value


```js
pipe(
  curl({
    url: "https://jsonplaceholder.typicode.com/posts",
    method: "post",
    data: {
        name: 'foo',
        body: 'bar',
        userId: 1
      }
  }), 
  alert({
    title: "API is down or buggy",
    trigger: function (curlOutput) {
      return (curlOutput.status != 201)
        || !curlOutput.json
        || (curlOutput.json.name !== "foo");
    },
    toggle: true
  })
).run();
```
<!-- example-end -->

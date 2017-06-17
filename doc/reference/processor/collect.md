## `collect`

Collects the last `count` received inputs.

### Input and output

* Category: Filter
* Input: Any value or object
* Output: Array of [SeriesObject](../programming.md#seriesobject) objects

### Synopsis

```js
collect(count)
collect(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `count` | number of inputs to collect | Number | *Mandatory* |
 
### Output array
 
The `collect` processor generates an array of [SeriesObject](../programming.md#seriesobject) objects representing the last `count` inputs,
ordered from oldest to newest.

### Description

The `collect` processor first waits until it has received `count` inputs. It then generates an array of SeriesObject
objects representing these inputs. When a new input is received, a new array with the last `count` inputs is generated.

### Examples

<!-- example-begin -->
#### Trigger an alert if three consecutive http requests at a 30 second interval are unsuccessful

```js
pipe(
		curl({ url: "http://www.mywebsite.com", period: "30s" }),
		collect(3),
		alert({
			title: "Website is down",
			trigger: function (curlOutputArray) {
				return curlOutputArray.every(function (seriesObject) {
					return seriesObject.value.status != 200;
				});
			},
			toggle: true
		})
).run();
```
<!-- example-end -->

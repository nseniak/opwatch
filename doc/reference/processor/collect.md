## `collect`

Collects the last `count` received inputs.

### Input and output

* Category: Filter
* Input: Any
* Output: Array of SeriesObject objects

### Synopsis

```js
collect(count)
collect(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `count` | number of inputs to collect | Number | *mandatory* |
 
 ### Output object
 
 The `collect` processor generates an array of SeriesObject objects representing the last `count` inputs,
 ordered from oldest to newest. Each SeriesObject object has the following fields:
 
| Property | Description | Type |
| :--- | :--- | :--- | :--- |
| `value` | the value of the input | Object |
| `timestamp` | time at which the input was received | Number |

### Description

The `collect` processor first waits until it has received `count` inputs. It then generates an array of SeriesObject
objects representing these inputs. When a new input is received, a new array with the last `count` inputs is generated.

### Examples

#### Trigger an alert if three consecutive http requests at a 10 second interval are unsuccessful

```js
pipe(
		curl({ url: "http://www.mywebsite.com", period: "10s" }),
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
## `alert`

Triggers an alert

### Input and output

* Category: Consumer
* Input: Any
* Output: N/A

### Synopsis

```js
alert(title)
alert(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `title` | alert title | String | *Mandatory* | 
| `level` | alert level, one of: `"lowest"`, `"low"`, `"medium"`, `"high"`, `"emergency"`  | String | `"medium"` |
| `details` | alert detailed information | Object or (input, payload) => Object | `(input, payload) => input` | 
| `trigger` | callback that determines if the alarm must be triggered | (input, payload) => Boolean | *None* |
| `toggle` | determines if the alert is a toggle | Boolean | `false` |
| `channel` | name of the channel where the alert is published | String | the default application channel |

### Description

The `alert` processor triggers an alert.

If the `channel` property is provided, the alert is displayed on this channel. Otherwise, the alert is displayed on the 
channel specified by `applicationChannel` in the [channel configuration](../channels.md).

#### How alerts are displayed

An alert is displayed on its channel as a message combining the `title`, `level` and `details` 
properties. The specific way these properties are displayed depends on the type of channel; however the following 
general display rules apply to all channel types:

* If the channel supports a notion of level, (a.k.a. severity or priority), the alert level is assigned to it; 
* If `details` is a [primitive value](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Data_structures) 
  (e.g, String or Number), it is displayed as plain text. If it is an Object, it is displayed as a list of 
  property/value pairs. If it is null, nothing is displayed.
* The title and details text are truncated to the maximum length supported by the channel, which depends on
  the channel type. To ensure that an alert can be displayed on any channel, keep the title below 200 characters and the 
  details below 500 characters. These are the limits of the most constraining channel type (Pushover). 
* The displayed message includes the hostname of the Opwatch process that generated the alert, except for the 
  console channel, where this information would be redundant.

For information about the different types of channels, see (channel)[../channels.md].

#### Alert details

If the `details` property is provided, it can be either:

* A callback, which is then invoked when the processor received an input to compute the alert details;
* Or a non-function value, which is used as the alert details, independently of any input.

If `details` is not provided, the alert's details are the `alert` processor input.

For example, this code displays an alert whose details are the output of [`df`](df.md):

```js
pipe(df("/tmp"), alert({
	title: "not enough space left",
	trigger: function (input) { return input.usageRatio > .8; }
})).run();
```

This code displays an alert whose details are the disk usage ratio:

```js
pipe(df("/tmp"), alert({
	title: "not enough space left",
	details: function (input) { return input.usageRatio; }
	trigger: function (input) { return input.usageRatio > .8; }
})).run();
```

This code displays an alert whose details are the text `"/tmp"`:

```js
pipe(df("/tmp"), alert({
	title: "not enough space left",
	details: "/tmp",
	trigger: function (input) { return input.usageRatio > .8; }
})).run();
```

#### Trigger

The optional `trigger` property contains a callback that is called on the processor's input and payload. If it
returns `true`, the alert is triggered; otherwise, it is not triggered.

#### Toggle mode

If `toggle` is `true`, the alert is handled as an on-off toggle; the `trigger` callback must then be provided.

A toggle alert is initially Off. As soon as the `trigger` callback returns `true` for a processor's input, the alert 
is turned on, and it stays On as long as the callback returns `true` for subsequent inputs. As soon as the 
`trigger` callback returns `false`, the alarm is turned off.

When an alarm is turned on or off, a corresponding message is displayed on the channel.

For example, this code displays an "on" alert message when the `/tmp` volume reaches 80% usage, and an "off"
alert message when it goes back under 80%.

```js
pipe(df("/tmp"), alert({
	title: "not enough space left",
	trigger: function (input) { return input.usageRatio > .8; },
	toggle: true
})).run();
```

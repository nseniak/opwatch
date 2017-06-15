# Channels

Opwatch displays alerts and other messages on *channels*. Opwatch supports the following types of channels:

* The *Console* channel, which prints alerts on the standard output;
* *[Slack](https://slack.com/)* channels, which displays alerts as messages in Slack;
* *[Pushover](https://pushover.net/)* channels, which displays alerts as Pushover notifications;
* *Remote* channels, which send alerts to other Opwatch servers in order to let them display them.

## Channel configuration

The channels to be used by Opwatch are configured using the following function.

### Function: `config.channels(configuration)`

This function configures the channels used by Opwatch to publish alarms and messages. 

#### Arguments

| Argument | Description | Type | Default |
| :--- | :--- | :--- |
| `configuration` | channel configuration object | Configuration object | *Mandatory* |

The configuration object has the following fields:

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `services` | service configurations | Service configuration object | *Mandatory* | 
| `applicationChannel` | name of the default channel used for alerts triggered by the `alert` processor | String | *Mandatory* | 
| `systemChannel` | name of the channel used for alerts triggered by the Opwatch framework (e.g., errors) | String | *Mandatory* |
| `fallbackChannel` | name of the channel used for alerts when the default channel fails for a reason or another | String | *Mandatory* | 

The `services` object
 
| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `console` | service configurations | Service configuration object | *Mandatory* | 
| `slack` | name of the default channel used for alerts triggered by the `alert` processor | String | *Mandatory* | 
| `pushover` | name of the channel used for alerts triggered by the Opwatch framework (e.g., errors) | String | *Mandatory* |

 
```js
"<channel type name>" : <channel definition> 
```

## Console

Opwatch has a predefined Console channel called `"console"`.

## Pushover

To define a Pushover channel, you need to create a Pushover account and a Pushover ppplication

- A Pushover API token, which identifies the Pushover application;
- A Pushover user key, which identifies the user or group of users who receive the push notifications.

```json
{
  "pushover": {
    "channels": {
      "<channel name 1>": {
        "apiToken": "<Pushover API token 1>",
        "userKey": "<Pushover user key 1>"
      },
      "<channel name 2>": {
        "apiToken": "<Pushover API token 2>",
        "userKey": "<Pushover user key 2>"
      }
      // add Pushover channels here
    }
  }
}
```

## Slack

```json
{
  "slack": {
    "channels": {
      "<channel name 1>": {
        "webhookUrl": "<webhook URL 1>"
      },
      "<channel name 2>": {
        "webhookUrl": "<webhook URL 2>"
      }
      // add Slack channels here
    }
  }
}
```

## Remote

```json
{
  "remote": {
    "channels": {
      "<channel name 1>": {
        "hostname": "<host on which the remote Opwatch instance is running>",
        "port": <http port of the remote Opwatch instance)>,
        "channel": "<name of the channel on the remote Opwatch instance>"
      },
      "<channel name 2>": {
        "hostname": "<host on which the remote Opwatch instance is running>",
        "port": <http port of the remote Opwatch instance)>,
        "channel": "<name of the channel on the remote Opwatch instance>"
      }
      // add remote channels here
    }
  }
}
```
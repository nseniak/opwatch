## `stdout`

Writes its input to a log file.

### Input and output

* Category: Consumer
* Input: Any value or object
* Output: None

### Synopsis

```js
log(file)
log(configuration_object)
```

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `file` | path of the log file | String | *Mandatory* | 
| `maxSize` | size at which a log file is archived | String | `"1mb"` | 
| `maxTotalSize` | total size of all archive files after which the oldest files are deleted | String | `"10mb"` | 
| `maxHistory` | maximum number of archive files to keep | Number | `10` | 
| `compression` | compression algorithm to use: either `"gz"`, `"zip"` or `""` for no compression | String | `"gz"` | 

### Description

The `log` processor converts its input to text using `JSON.stringify` and writes it to a log file, preceded
by a timestamp. Log files are archived, compressed and capped to a total maximum to avoid taking up too much disk 
space.

### Examples

#### Write system load information to a log file

```js
pipe(
	top(),
	log("logs/top.log")
).run();
```

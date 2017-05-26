## `jstack`

Parses java Java exception stack.

### Input and output

* Category: Filter
* Input: String representing a line of text
* Output: Exception object

### Synopsis

```js
jstack()
jstack(configuration_object)
```

### Configuration properties

| Property | Description | Type | Default |
| :--- | :--- | :--- | :--- |
| `methodRegexp` | regexp used to select the stack trace element | RegExp | *Optional* |
 
 ### Exception object properties
 
| Property | Description | Type |
| :--- | :--- | :--- | :--- |
| `exceptionClass` | fully qualified name of the exception class | String |
| `exceptionMessage` | exception message | String |
| `method` | fully qualified name of the method throwing the exception | String |
| `location` | source location of the code throwing the exception | String |
| `previousLine` | last non empty line preceding the stack trace | String |
| `stack` | exception stack trace | Array of String |

### Description

The `jstack` processor analyzes the lines of text it receives and attempts to recognize Java stack traces.
When a stack trace is recognized, an Exception object is produced. Java stack traces don't have an "end"
marker, thus the Exception object is generated when the first line not belonging to the stack trace is met (for
example, an empty line).

The `method` and `location` fields of the Exception object provide information about the code that threw the exception. 
If the `methodRegexp` configuration property is not provided, this location is extracted from the first element
of the stack trace. For example, the following stack trace:

```
java.io.FileNotFoundException: file.txt
        at java.io.FileInputStream.<init>(FileInputStream.java)
        at java.io.FileInputStream.<init>(FileInputStream.java)
        at org.myproject.FileReader.readMyFile(FileReader.java:19)
        at org.myproject.Main.main(Main.java:27)
```

yields the following Exception object: 

```json
{
    "exceptionClass": "java.io.FileNotFoundException",
    "exceptionMessage": "file.txt",
    "method": "java.io.FileInputStream.<init>",
    "location": "FileInputStream.java",
    "previousLine": "",
    "stack": [
      "java.io.FileNotFoundException: file.txt",
      "        at java.io.FileInputStream.<init>(FileInputStream.java)",
      "        at java.io.FileInputStream.<init>(FileInputStream.java)",
      "        at org.myproject.FileReader.readMyFile(FileReader.java:19)",
      "        at org.myproject.Main.main(Main.java:27)"
    ]
}
```

If the `methodRegexp` configuration property is provided, `method` and `location` are extracted from 
the first stack element whose method name is matched by the regexp. This is useful for generating Exception 
objects with locations in your own application's code, as opposed to third-party libraries or the JDK.
For example, `jstack({ methodRegexp: /^org\.myproject\./ })` applied to the same stack trace generates the
following object:
 
```json
{
    "exceptionClass": "java.io.FileNotFoundException",
    "exceptionMessage": "file.txt",
    "method": "org.myproject.FileReader.readMyFile",
    "location": "FileReader.java:19",
    "previousLine": "",
    "stack": [
      "java.io.FileNotFoundException: file.txt",
      "        at java.io.FileInputStream.<init>(FileInputStream.java)",
      "        at java.io.FileInputStream.<init>(FileInputStream.java)",
      "        at org.myproject.FileReader.readMyFile(FileReader.java:19)",
      "        at org.myproject.Main.main(Main.java:27)"
    ]
}
```
 
If no stack element is matched by `methodRegexp`, `method` and `location` are extracted from the first stack element.

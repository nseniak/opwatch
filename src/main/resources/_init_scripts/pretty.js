/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

__prettyObject = (function () {

	var invalidJsonObjectResult = "not_a_json_object";

	var prettyObject = function (object, depth, options) {
		if (depth > options.maxDepth) {
			return "...";
		}
		var type = Object.prototype.toString.call(object);
		if (type === "[object Null]") {
			return "null";
		} else if (type === "[object Undefined]") {
			return prettyUndefined(object, depth, options);
		} else if (type === "[object Boolean]") {
			return prettyBoolean(object, depth, options);
		} else if (type === "[object Number]"
				|| (type === "[object java.lang.Long]")
				|| (type === "[object java.lang.Integer]")
				|| (type === "[object java.lang.Float]")
				|| (type === "[object java.lang.Double]")) {
			return prettyNumber(object, depth, options);
		} else if (type === "[object String]") {
			return prettyString(object, depth, options);
		} else if (type === "[object Date]") {
			return prettyDate(object, depth, options);
		} else if (type === "[object RegExp]") {
			return prettyRegExp(object, depth, options);
		} else if ((type === "[object Function]")
				|| (type === "[object org.opwatch.processor.config.JavascriptPredicate]")
				|| (type === "[object org.opwatch.processor.config.JavascriptFilter]")
				|| (type === "[object org.opwatch.processor.config.JavascriptProducer]")) {
			return prettyFunction(object, depth, options);
		} else if ((type === "[object Array]") || type ==="[object java.util.ArrayList]" || object.__payloadArray) {
			return prettyArray(object, depth, options);
		} else if (type.startsWith("[object org.opwatch.processor.") && object.processor) {
			return prettyProcessor(object, depth, options);
		} else if (type === "[object org.opwatch.processor.config.Duration]") {
			return prettyDuration(object, depth, options);
		} else if (type === "[object org.opwatch.processor.config.ValueOrFilter]") {
			return prettyValueOrFilter(object, depth, options);
		} else if (type === "[object org.opwatch.processor.config.ValueOrList]") {
			return prettyValueOrList(object, depth, options);
		} else {
			return prettyObjectProperties(object, depth, options);
		}
	};

	var prettyString = function (string, depth, options) {
		return JSON.stringify(string);
	};

	var prettyDate = function (date, depth, options) {
		if (options.json) {
			return JSON.stringify(date);
		} else {
			return "new Date(" + JSON.stringify(object.toString()) + ")";
		}
	};

	var prettyNumber = function (number, depth, options) {
		return number.toString();
	};

	var prettyBoolean = function (boolean, depth, options) {
		return boolean.toString();
	};

	var prettyUndefined = function (undef, depth, options) {
		if (options.json) {
			return invalidJsonObjectResult;
		} else {
			return "undefined";
		}
	};

	var prettyFunction = function (fn, depth, options) {
		if (options.json) {
			return invalidJsonObjectResult;
		} else {
			return fn.toString();
		}
	};

	var prettyRegExp = function (regexp, depth, options) {
		if (options.json) {
			return "{}";
		} else {
			return regexp.toString();
		}
	};

	var prettyValueOrFilter = function (object, depth, options) {
		var obj = (object.type == "value") ? object.value : object.filter;
		return prettyObject(obj, depth, options);
	};

	var prettyValueOrList = function (object, depth, options) {
		if (object.type == "value") {
			return prettyObject(object.value, depth, options);
		} else {
			return prettyArray(object.list, depth, options);
		}
	};

	var prettyArray = function (array, depth, options) {
		if (array.length === 0) {
			return "[]";
		} else if (array.length === 1) {
			return "[" + options.space + prettyObject(array[0], depth + 1, options) + options.space + "]";
		} else {
			var lines = [];
			lines.push("[");
			var prefix = indent(depth + 1, options);
			for (var i = 0; i < array.length; i++) {
				if (i > options.maxArrayLength) {
					lines.push(prefix + "...");
					break;
				}
				var element = prettyObject(array[i], depth + 1, options);
				var comma = (i < array.length - 1) ? "," : "";
				lines.push(prefix + element + comma);
			}
			lines.push(indent(depth, options) + "]");
			return joinLines(lines, options);
		}
	};

	var prettyObjectProperties = function (object, depth, options) {
		var properties = [];
		for (property in object) {
			if (!object.hasOwnProperty // Java object
					|| object.hasOwnProperty(property)) {
				properties.push(property);
			}
		}
		return prettyProperties(object, definedProperties(object, properties), depth, options);
	}

	var prettyProperties = function (object, properties, depth, options) {
		if (properties.length === 0) {
			return "{}";
		} else if (properties.length === 1) {
			return "{" + options.space
					+ prettyProperty(properties[0], options) + ":" + options.space + prettyObject(object[properties[0]], depth + 1, options)
					+ options.space + "}";
		} else {
			var lines = [];
			lines.push("{");
			var prefix = indent(depth + 1, options);
			for (var i = 0; i < properties.length; i++) {
				var value = prettyObject(object[properties[i]], depth + 1, options);
				var comma = (i < properties.length - 1) ? "," : "";
				var property = prettyProperty(properties[i], options);
				lines.push(prefix + property + ":" + options.space + value + comma);
			}
			lines.push(indent(depth, options) + "}");
			return joinLines(lines, options);
		}
	};

	var prettyProcessor = function (processor, depth, options) {
		var config = processor.configuration;
		var properties = config.properties();
		var factory = processor.factory;
		var name = factory.name();
		if ((name === "alias") && !options.expandAlias) {
			return config.name + '(' + prettyObjectProperties(config.configuration, depth + 1, options) + ')';
		} else {
			return name + "(" + prettyProperties(config, definedProperties(config, properties), depth + 1, options) + ")";
		}
	};

	var prettyProperty = function (property, options) {
		if (options.json) {
			return "\"" + property + "\"";
		} else {
			return property;
		}
	};

	var definedProperties = function (object, properties) {
		var nonNull = [];
		for (var i = 0; i < properties.length; i++) {
			if ((object[properties[i]] !== null) && (object[properties[i]] !== undefined)) {
				nonNull.push(properties[i]);
			}
		}
		return nonNull;
	};

	var prettyDuration = function (duration, depth, options) {
		if (duration.milliseconds) {
			return prettyNumber(duration.milliseconds, depth, options);
		} else {
			return prettyString(duration.text, depth, options);
		}
	};

	var indentations = {};

	var indent = function (depth, options) {
		if (options.indent) {
			if (!indentations[depth]) {
				var dst = "";
				for (var index = 0; index < depth * options.indent; index += 1) {
					dst += " ";
				}
				indentations[depth] = dst;
			}
			return indentations[depth];
		} else {
			return "";
		}
	};

	var joinLines = function (array, options) {
		if (options.indent) {
			return array.join("\n");
		} else {
			return array.join(options.space);
		}
	}

	return prettyObject;
})();

function pretty(object, options) {
	var actualOptions = {
		indent: 2,
		expandAlias: false,
		maxDepth: 5,
		maxArrayLength: 100,
		json: false,
		space: " ",
		asString: false
	};
	if (options) {
		for (var property in options) {
			actualOptions[property] = options[property];
		}
	}
	var str;
	try {
		str = __prettyObject(object, 0, actualOptions);
	} catch (error) {
		str = "<cannot pretty print object>";
	}
	if (actualOptions.asString) {
		return str;
	} else {
		print(str);
		return undefined;
	}
}

function __json_stringify(object) {
	return pretty(object, {indent: 0, json: true, asString: true, maxArrayLength: 1000, maxDepth: 20, space: ""})
}

function __json_parse(object) {
	return JSON.parse(object);
}

function __pretty_stringify(object) {
	return pretty(object, {asString: true});
}

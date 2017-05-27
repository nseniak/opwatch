pretty = (function () {

	var prettyObject = function (object, depth, options) {
		if (depth > options.maxDepth) {
			return "...";
		}
		var type = Object.prototype.toString.call(object);
		if (type === "[object Null]") {
			return "null";
		} else if (type === "[object Undefined]") {
			return "undefined";
		} else if (type === "[object Boolean]") {
			return object.toString();
		} else if (type === "[object Number]"
				|| (type === "[object java.lang.Long]")
				|| (type === "[object java.lang.Integer]")
				|| (type === "[object java.lang.Float]")
				|| (type === "[object java.lang.Double]")) {
			return prettyNumber(object, depth, options);
		} else if (type === "[object String]") {
			return prettyString(object, depth, options);
		} else if (type === "[object Date]") {
			return "new Date(" + JSON.stringify(object.toString()) + ")";
		} else if (type === "[object RegExp]") {
			return object.toString();
		} else if ((type === "[object Function]")
				|| (type === "[object org.opwatch.processor.config.JavascriptPredicate]")
				|| (type === "[object org.opwatch.processor.config.JavascriptFilter]")
				|| (type === "[object org.opwatch.processor.config.JavascriptProducer]")) {
			return prettyFunction(object);
		} else if ((type === "[object Array]") || object.__payloadArray) {
			return prettyArray(object, depth, options);
		} else if (type.startsWith("[object org.opwatch.processor.") && object.processor) {
			return prettyProcessor(object, depth, options);
		} else if (type === "[object org.opwatch.processor.config.Duration]") {
			return prettyDuration(object, depth, options);
		} else if (type === "[object org.opwatch.processor.config.ConstantOrFilter]") {
			return prettyConstantOrFilter(object, depth, options);
		} else {
			return prettyObjectProperties(object, depth, options);
		}
	};

	var prettyString = function (string, depth, options) {
		return JSON.stringify(string);
	};

	var prettyNumber = function (number, depth, options) {
		return number.toString();
	};

	var prettyFunction = function (fn) {
		return fn.toString();
	};

	var prettyConstantOrFilter = function (object, depth, options) {
		var obj = (object.type == "constant") ? object.constant : object.filter;
		return prettyObject(obj, depth, options);
	};

	var prettyArray = function (array, depth, options) {
		if (array.length === 0) {
			return "[]";
		} else if (array.length === 1) {
			return "[ " + prettyObject(array[0], depth + 1, options) + " ]";
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
		return prettyProperties(object, properties, depth, options);
	}

	var prettyProperties = function (object, properties, depth, options) {
		if (properties.length === 0) {
			return "{}";
		} else if (properties.length === 1) {
			return "{ " + properties[0] + ": " + prettyObject(object[properties[0]], depth + 1, options) + " }";
		} else {
			var lines = [];
			lines.push("{");
			var prefix = indent(depth + 1, options);
			for (var i = 0; i < properties.length; i++) {
				var value = prettyObject(object[properties[i]], depth + 1, options);
				var comma = (i < properties.length - 1) ? "," : "";
				lines.push(prefix + properties[i] + ": " + value + comma);
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
			return name + "(" + prettyProperties(config, nonNullProperties(config, properties), depth + 1, options) + ")";
		}
	};

	var nonNullProperties = function (object, properties) {
		var nonNull = [];
		for (var i = 0; i < properties.length; i++) {
			if (object[properties[i]] !== null) {
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
	}

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
			return array.join(" ");
		}
	}

	return function (object, options) {
		var actualOptions = {indent: 2, expandAlias: false, maxDepth: 5, maxArrayLength: 100};
		if (options) {
			for (var property in options) {
				actualOptions[property] = options[property];
			}
		}
		try {
			return prettyObject(object, 0, actualOptions);
		} catch (error) {
			return "<cannot pretty print object>";
		}
	};
})();

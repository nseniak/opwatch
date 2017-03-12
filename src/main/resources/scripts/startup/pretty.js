pretty = function (jsObject, expandAlias, indentLength) {
	var indentString,
			fullFunction = true,
			newLine,
			newLineJoin,
			TOSTRING,
			TYPES,
			valueType,
			processor,
			repeatString,
			prettyObjectPrint,
			prettyArray,
			functionSignature,
			pretty,
			visited;

	TOSTRING = Object.prototype.toString;

	TYPES = {
		'undefined': 'undefined',
		'number': 'number',
		'boolean': 'boolean',
		'string': 'string',
		'[object Function]': 'function',
		'[object RegExp]': 'regexp',
		'[object Array]': 'array',
		'[object Date]': 'date',
		'[object Error]': 'error'
	};

	valueType = function (o) {
		var type = TYPES[typeof o] || TYPES[TOSTRING.call(o)] || processor(o) || processorFunction(o) || stringConstant(o) || stringProducer(o) || (o ? 'object' : 'null');
		return type;
	};

	processor = function (d) {
		if (d && d.type && d.descriptor) {
			return "processor"
		} else {
			return null;
		}
	}

	processorFunction = function (d) {
		if (d && d.function) {
			return "processorFunction"
		} else {
			return null;
		}
	}

	stringConstant = function (d) {
		if (d && d.constant) {
			return "stringConstant"
		} else {
			return null;
		}
	}

	stringProducer = function (d) {
		if (d && d.producer) {
			return "stringProducer"
		} else {
			return null;
		}
	}

	repeatString = function (src, length) {
		var dst = '',
				index;
		for (index = 0; index < length; index += 1) {
			dst += src;
		}

		return dst;
	};

	prettyObjectPrint = function (object, indent) {
		var value = [],
				property;

		indent += indentString;
		for (property in object) {
			if (object.hasOwnProperty(property)) {
				value.push(indent + property + ': ' + pretty(object[property], indent));
			}
		}

		return value.join(newLineJoin) + newLine;
	};

	prettyProcessorPrint = function (processor, indent) {
		var value = [],
				descriptor,
				properties,
				property,
				i;

		descriptor = processor.descriptor;
		if ((processor.type == "alias") && !expandAlias) {
			return descriptor.name + '({' + newLine + prettyObjectPrint(descriptor.descriptor, indent) + indent + '})';
		} else {
			indent += indentString;
			properties = descriptor.properties();
			for (i = 0; i < properties.length; i++) {
				property = properties[i];
				if ((property != "class") && descriptor[property]) {
					value.push(indent + property + ': ' + pretty(descriptor[property], indent));
				}
			}
			return processor.type + '({' + newLine + value.join(newLineJoin) + newLine + indent + '})';
		}
	}

	prettyArray = function (array, indent) {
		var index,
				length = array.length,
				value = [];

		indent += indentString;
		for (index = 0; index < length; index += 1) {
			value.push(pretty(array[index], indent, indent));
		}

		return value.join(newLineJoin) + newLine;
	};

	functionSignature = function (element) {
		var signatureExpression,
				signature;

		element = element.toString();
		signatureExpression = new RegExp('function\\s*.*\\s*\\(.*\\)');
		signature = signatureExpression.exec(element);
		signature = signature ? signature[0] : '[object Function]';
		return fullFunction ? element : '"' + signature + '"';
	};

	pretty = function (element, indent, fromArray) {
		var type;

		type = valueType(element);
		fromArray = fromArray || '';
		if (visited.indexOf(element) === -1) {
			switch (type) {
				case 'array':
					visited.push(element);
					return fromArray + '[' + newLine + prettyArray(element, indent) + indent + ']';

				case 'boolean':
					return fromArray + (element ? 'true' : 'false');

				case 'date':
					return fromArray + '"' + element.toString() + '"';

				case 'number':
					return fromArray + element;

				case 'object':
					visited.push(element);
					return fromArray + '{' + newLine + prettyObjectPrint(element, indent) + indent + '}';

				case 'string':
					return fromArray + JSON.stringify(element);

				case 'function':
					return fromArray + functionSignature(element);

				case 'processorFunction':
					return fromArray + functionSignature(element.function);

				case 'stringConstant':
					return fromArray  + JSON.stringify(element.constant);

				case 'stringProducer':
					return fromArray  + functionSignature(element.producer.function);

				case 'undefined':
					return fromArray + 'undefined';

				case 'processor':
					return fromArray + prettyProcessorPrint(element, indent);

				case 'null':
					return fromArray + 'null';

				default:
					if (element.toString) {
						return fromArray + '"' + element.toString() + '"';
					}
					return fromArray + '<<<ERROR>>> Cannot get the string value of the element';
			}
		}
		return fromArray + 'circular reference to ' + element.toString();
	};

	if (jsObject) {
		if (indentLength === undefined) {
			indentLength = 4;
		}

		indentString = repeatString(' ', indentLength);
		newLine = '\n';
		newLineJoin = ',' + newLine;
		visited = [];
		return pretty(jsObject, '') + newLine;
	}

	return 'Error: no Javascript object provided';
};

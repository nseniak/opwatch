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

function factory_wrapper(factory) {
	var ctor = function () {
		return make_processor(factory, arguments);
	};
	ctor.help = function () {
		printHelp(factory.schema(), false);
	};
	ctor.factory = factory;
	return ctor;
}

function make_processor(factory, args) {
	var schema;
	if (args.length > 1) {
		factory.error("too many arguments");
	}
	if (args.length == 0) {
		schema = {};
	} else if (args[0].constructor == Object) {
		schema = args[0];
	} else {
		implicit = implicit_property(factory.schema());
		if (implicit) {
			schema = {};
			schema[implicit.name] = args[0];
		} else {
			factory.error("doesn't have an implicit property; you must pass a full configuration object");
		}
	}
	return factory.make(schema);
}

function implicit_property(schema) {
	var props = schema.properties;
	for (var i = 0; i < props.length; i++) {
		if (props[i].implicit) {
			return props[i];
		}
	}
}

var tab1 = "   ";
var tab2 = tab1 + tab1;

function printHelp(schema, varargs) {
	print("Category: " + schema.category);
	print();
	print("Invocation syntax:");
	print();
	var props = schema.properties;
	if (varargs) {
		print(tab1 + schema.name + "(<processor>, ...);");
	}
	var implicit = implicit_property(schema);
	var mandatory = [];
	var optional = [];
	for (var i = 0; i < props.length; i++) {
		var prop = props[i];
		if (prop.implicit) {
			implicit = prop;
		} else if (prop.optional) {
			optional.push(prop);
		} else {
			mandatory.push(prop);
		}
	}
	mandatory.sort(prop_compare);
	optional.sort(prop_compare);
	if (implicit) {
		print(tab1 + schema.name + "(" + implicit.name + " <" + implicit.type + ">);");
	}
	var all = [];
	if (implicit) {
		all.push(implicit);
	}
	all = all.concat(mandatory);
	all = all.concat(optional);
	if (all.length == 0) {
		print(tab1 + schema.name + "();");
	} else {
		print(tab1 + schema.name + "({");
		for (var i = 0; i < all.length; i++) {
			var prop = all[i];
			print(tab2 + property_help(prop));
		}
		print(tab1 + "});");
	}
}

function prop_compare(p1, p2) {
	if (p1.name < p2.name) {
		return -1;
	} else if (p1.name > p2.name) {
		return 1;
	} else {
		return 0;
	}
}

function property_help(prop) {
	var opt_left = prop.optional ? "[ " : "";
	var prettyString = pretty(prop.defaultValue, { asString: true });
	var opt_right = prop.optional ? ((prop.defaultValue !== null) ? " = " + prettyString : "") + " ]" : "";
	return opt_left + prop.name + " <" + prop.type + ">" + opt_right;
}

function vararg_factory_wrapper(factory) {
	var ctor = function () {
		return vararg_make_processor(factory, arguments);
	}
	ctor.help = function () {
		printHelp(factory.schema(), true);
	}
	return ctor;
}

function vararg_make_processor(factory, args) {
	var config;
	if (args.length == 1) {
		var arg = args[0];
		if (arg.constructor == Array) {
			config = {processors: arg};
		} else if (arg.isprocessor) {
			config = {processors: [arg]};
		} else {
			config = arg;
		}
	} else {
		config = {processors: Array.prototype.slice.call(args)};
	}
	return factory.make(config);
}
//
function help() {
	print("Predefined processors by category:");
	var factories = __factories();
	var by_category = {};
	for (var i = 0; i < factories.length; i++) {
		var fact = factories[i];
		var schema = fact.schema();
		var category = schema.category;
		if (by_category[category]) {
			by_category[category].push(schema);
		} else {
			by_category[category] = [schema];
		}
	}
	for (category in by_category) {
		print(tab1 + capitalizeFirstLetter(category) + ":");
		by_category[category].map(function (schema) {
			print(tab2 + processor_short_help(schema));
		})
	}
	print("Type processor.help() for help about a specific processor");
}

function processor_short_help(schema) {
	var all = [];
	var props = schema.properties;
	for (var i = 0; i < props.length; i++) {
		all.push(props[i]);
	}
	all.sort(prop_compare);
	var conf;
	if (all.length == 0) {
		conf = "{}";
	} else {
		conf = "{ " + all.map(function (prop) {
					return prop.name;
				}).join(", ") + " }";
	}
	return schema.name + "(" + conf + ")";
}

function capitalizeFirstLetter(string) {
	return string.charAt(0).toUpperCase() + string.slice(1);
}


function merge_config() {
	var res = {};
	for (var i = 0; i < arguments.length; i++) {
		var config = arguments[i];
		for (p in config) {
			res[p] = config[p];
		}
	}
	return res;
}

function stats(data) {
	return __stats(__service, data);
}

function stop(returnValue) {
	__service.stopWithReturnValue(returnValue);
	return undefined;
}

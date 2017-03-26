//
Packages.com.untrackr.alerter.service.ScriptService.logInfo("JavaScript module path: " + require.paths());
//
function factory_wrapper(factory) {
	var ctor = function (arg) {
		return make_processor(factory, arg);
	}
	ctor.help = function () {
		printHelp(factory.schema(), false);
	}
	return ctor;
}

function make_processor(factory, arg) {
	var schema;
	if (!arg) {
		schema = {};
	} else if (arg.constructor == Object) {
		schema = arg;
	} else {
		implicit = implicit_property(factory.schema());
		if (implicit) {
			schema = {};
			schema[implicit.name] = arg;
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

function printHelp(schema, varargs) {
	var props = schema.properties;
	if (varargs) {
			print(schema.name + "(<processor>, ...);");
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
		print(schema.name + "(" + implicit.name + " <" + implicit.type + ">);");
	}
	var all = [];
	if (implicit) {
		all.push(implicit);
	}
	all = all.concat(mandatory);
	all = all.concat(optional);
	print(schema.name + "({");
	for (var i = 0; i < all.length; i++) {
		var prop = all[i];
		print("   " + property_help(prop));
	}
	print("});");
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
	var opt_right = prop.optional ? ((prop.defaultValue != null) ? " = " + JSON.stringify(prop.defaultValue) : "") + " ]" : "";
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
	print("Processor constructors:");
	var factories = __factories(1);
	for (var i = 0; i < factories.length; i++) {
		var fact = factories[i];
		print("   " + constructor_short_help(fact.schema()));
	}
}

function constructor_short_help(schema) {
	var all = [];
	var props = schema.properties;
	for (var i = 0; i < props.length; i++) {
		all.push(props[i]);
	}
	all.sort(prop_compare);
	var conf = "{ " + all.map(function (prop) { return prop.name; }).join(", ") + " }";
	return schema.category + ": " + schema.name + "(" + conf + ")";
}
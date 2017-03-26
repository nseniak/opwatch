//
Packages.com.untrackr.alerter.service.ScriptService.logInfo("JavaScript module path: " + require.paths());
//
function factory_wrapper(factory) {
	var ctor = function (arg) {
		return make_processor(factory, arg);
	}
	ctor.help = function () {
		printHelp(factory, false);
	}
	return ctor;
}

function make_processor(factory, arg) {
	var config;
	if (!arg) {
		config = {};
	} else if (arg.constructor == Object) {
		config = arg;
	} else {
		implicit = implicit_property(factory);
		if (implicit) {
			config = {};
			config[implicit.name] = arg;
		} else {
			factory.error("doesn't have an implicit property; you must pass a full configuration object");
		}
	}
	return factory.make(config);
}

function implicit_property(factory) {
	var props = factory.config().properties;
	for (var i = 0; i < props.length; i++) {
		if (props[i].implicit) {
			return props[i];
		}
	}
}

function printHelp(factory, varargs) {
	var props = factory.config().properties;
	if (varargs) {
		print(factory.name() + "(<processor>);");
	}
	var implicit = implicit_property(factory);
	if (implicit) {
		print(factory.name() + "(<" + implicit.name + ">);");
	}
	print(factory.name() + "({");
	for (var i = 0; i < props.length; i++) {
		var prop = props[i];
		var comma = (i < props.length -1) ? "," : "";
		var opt_left = prop.optional ? "[ " : "";
		var opt_right = prop.optional ? " ]" : "";
		print("   " + opt_left + prop.name + ": <" + prop.name + ">" + opt_right + comma);
	}
	print("});");
}

function define_varargs_constructor(name, factory) {
	name = vararg_factory_wrapper(factory);
}

function vararg_factory_wrapper(factory) {
	var ctor = function () {
		return vararg_make_processor(factory, arguments);
	}
	ctor.help = function () {
		printHelp(factory, true);
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

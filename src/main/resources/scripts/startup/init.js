//
Packages.com.untrackr.alerter.service.ScriptService.logInfo("JavaScript module path: " + require.paths());
//
function factory_wrapper(factory) {
	return function (arg) {
		return make_processor(factory, arg);
	}
}

function make_processor(factory, arg) {
	var options;
	if (!arg) {
		options = {};
	} else if (arg.constructor == Object) {
		options = arg;
	} else if (factory.defaultProperty()) {
		options = {};
		options[factory.defaultProperty()] = arg;
	} else {
		factory.error("doesn't have a default option; you must pass an object");
	}
	return factory.make(options);
}
//
function vararg_factory_wrapper(factory) {
	return function () {
		return vararg_make_processor(factory, arguments);
	}
}

function vararg_make_processor(factory, args) {
	var options;
	if (args.length == 1) {
		var arg = args[0];
		if (arg.constructor == Array) {
			options = { processors: arg };
		} else {
			options = arg;
		}
	} else {
		options = {processors: Array.prototype.slice.call(args)};
	}
	return factory.make(options);
}
//

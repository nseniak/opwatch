//
Packages.com.untrackr.alerter.service.ScriptService.logInfo("JavaScript module path: " + require.paths());
//
function factory_wrapper(factory) {
	return function (arg) {
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
}
//
function vararg_factory_wrapper(factory) {
	return function () {
		var options;
		if (arguments.length == 1) {
			var arg = arguments[0];
			if (arg.constructor == Array) {
				options = { processors: arg };
			} else {
				options = arg;
			}
		} else {
			options = {processors: Array.prototype.slice.call(arguments)};
		}
		return factory.make(options);
	}
}
//
function simple_wrapper(fn) {
	return function () {
		print(fn);
		print(Array.prototype.slice.call(arguments).length);
		return fn.apply(this, Array.prototype.slice.call(arguments));
	};
}
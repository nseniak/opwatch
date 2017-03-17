//
Packages.com.untrackr.alerter.service.ScriptService.logInfo("JavaScript module path: " + require.paths());
//
function factory_wrapper(factory) {
	return function (descriptor) {
		if (descriptor) {
			return factory(descriptor);
		} else {
			return factory({});
		}
	}
}
//
function vararg_factory_wrapper(factory) {
	return function () {
		if (arguments.length == 1) {
			var arg = arguments[0];
			if (arg && arg['processors']) {
				return factory(arg);
			}
		}
		return factory({processors: Array.prototype.slice.call(arguments)});
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
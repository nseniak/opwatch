exports.clock = function (config) {
	var i = 0;
	return alias({
		name: "clock",
		configuration: config,
		processor: call(merge_config(config, {
			output: function() {
				return i++;
			}
		}))
	})
};

exports.capture = function () {
	return alias({
		name: "capture",
		configuration: {},
		processor: pipe(apply(function (input, payload) {
			i = input;
			p = payload;
			interrupt();
		}), stdout())
	})
};

function clock(config) {
	var i = 0;
	return alias({
		name: "clock",
		configuration: config,
		processor: repeat(merge_config(config, {
			producer: function() {
				return i++;
			}
		}))
	})
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
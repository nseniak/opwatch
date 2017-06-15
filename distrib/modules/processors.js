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
}

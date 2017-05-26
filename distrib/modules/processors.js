exports.clock = function (config) {
	var i = 0;
	return alias({
		name: "clock",
		configuration: config,
		processor: repeat(merge_config(config, {
			lambda: function() {
				return i++;
			}
		}))
	})
}

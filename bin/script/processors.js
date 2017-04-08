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

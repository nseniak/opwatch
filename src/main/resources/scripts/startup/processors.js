function clock(config) {
	var i = 0;
	return alias({
		name: "clock",
		configuration: config,
		processor: repeat({
			// period: config.period,
			producer: function() {
				return i++;
			}
		})
	})
}
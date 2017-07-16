r = (function () {
	var generate = function () {
		var counter = 0;
		return call({
			output: function () {
				return counter++;
			},
			period: 1
		});
	};
	var total = 0;
	var max = 1000;
	var count = function (index) {
		var n = 0;
		return apply(
				function (input) {
					total = total + 1;
					n = n + 1;
					if (n <= max) {
						return {index: index, total: total, count: n};
					} else {
						return undefined;
					}
				}
		);
	};
	var last_output = [];
	return pipe(
			generate(),
			parallel(
					count(0),
					count(1),
					count(2),
					count(3),
					count(4)
			),
			apply(function (input) {
				last_output[input.index] = input;
				if (last_output.every(function (i) {
							return i && i.count === max;
						})) {
					stop(last_output);
				}
				return undefined;
			}),
			stdout()
	)
})();

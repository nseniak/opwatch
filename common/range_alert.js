// run(
// 		pipe(
// 				top({period: "1s"}),
// 				js({
// 					transformer: function (input) {
// 						return input.loadAverage;
// 					}
// 				}),
// 				trail({duration: "10s"}),
// 				stdout({}),
// 				alert({
// 					message: "load average going up!",
// 					predicate: function (input) {
// 						return input.regression.slope > .1;
// 					}
// 				})
// 		)
// );

function frequency_filter(desc) {
	var number = desc.count;
	var duration = desc.duration ? desc.duration : "1m";
	// return pipe(
	// 		count({duration: duration}),
	// 		jsgrep({
	// 			predicate: function (c) {
	// 				return c > number;
	// 			}
	// 		})
	// );
	return alias({
		name: 'frequency_filter',
		descriptor: desc,
		processor: pipe(
				count({duration: duration}),
				jsgrep({
					predicate: function (c) {
						return c > number;
					}
				})
		)
	});
}

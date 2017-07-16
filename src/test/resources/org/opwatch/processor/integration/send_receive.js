(function () {
	var n = 0;
	var max = 10;
	var received = [];
	return pipe(
			parallel(
					receive("test_path"),
					pipe(
							call({
								output: function () {
									var i = n++;
									if (i < max) {
										return {number: i, string: "string" + i};
									} else {
										return undefined;
									}
								},
								period: 5
							}),
							send({path: "test_path", hostname: "localhost", port: 8080})
					)
			),
			apply(function (input) {
				received.push(input);
				if (received.length === max) {
					stop(received);
				} else {
					return undefined;
				}
			}),
			stdout()
	);
})();

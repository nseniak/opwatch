(function () {
	var max = 20;
	var doneCount = 0;
	data = {foo: 0, bar: 0};
	function counter(str) {
		var n = 0;
		var sent = false;
		return pipe(
				sh("grep --line-buffered " + str),
				call({
					input: function (input) {
						if (data[str] < max) {
							data[str]++;
						}
					},
					output: function () {
						if ((data[str] === max) && !sent) {
							sent = true;
							return "done";
						} else {
							return undefined;
						}
					},
					period: "1s"
				})
		);
	}

	return pipe(
			parallel(
					sh("while true; do echo foo; sleep 0.2; done"),
					sh({command: "echo bar", period: 200})
			),
			parallel(
					counter("foo"),
					counter("bar")
			),
			apply(function (input) {
				doneCount = doneCount + 1;
				if (doneCount === 2) {
					stop(data);
				}
				return undefined;
			}),
			stdout()
	)
})();

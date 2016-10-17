// Must execute after jvm-npm.js
(function () {
	var alerterPaths = java.lang.System.getProperty('alerter.path');
	if (alerterPaths) {
		if (require.NODE_PATH) {
			require.NODE_PATH = alerterPaths + require.NODE_PATH;
		} else {
			require.NODE_PATH = alerterPaths;
		}
	}
	print("JavaScript module path: " + require.paths());
})();

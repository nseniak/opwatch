package com.untrackr.alerter;

import com.untrackr.alerter.common.ApplicationUtil;
import com.untrackr.alerter.service.ProcessorService;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.tomcat.ConnectorStartFailedException;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class AlerterApplication implements CommandLineRunner {

	@Autowired
	private ProcessorService processorService;

	@Autowired
	private ApplicationContext applicationContext;

	private static CommandLineOptions options;

	@Override
	public void run(String... args) throws Exception {
		processorService.run(options);
		SpringApplication.exit(applicationContext);
	}

	public static void main(String[] args) throws Exception {
		try {
			ApplicationUtil.checkProperty("app.log.dir", "logging directory");
			ApplicationUtil.checkProperty("app.log.basename", "logfile basename");
			options = parseOptions(args);
			if (options.getPort() != null) {
				System.setProperty("server.port", Integer.toString(options.getPort()));
			}
			new SpringApplicationBuilder(AlerterApplication.class).web(!options.isNoHttp()).run(args);
		} catch (ConnectorStartFailedException e) {
			System.err.println("Cannot start http server on port " + e.getPort());
			System.err.println("To specify another port, use -port <port>");
			System.err.println("To start without an http server, use -no-http");
			System.exit(1);
		} catch (Throwable t) {
			System.err.println(t.getMessage());
			System.exit(1);
		}
	}

	private static CommandLineOptions parseOptions(String[] argStrings) {
		OptionParser parser = new OptionParser();
		OptionSpec<String> hostname = parser.accepts("hostname").withRequiredArg().ofType(String.class);
		OptionSpec<String> initFile = parser.accepts("init").withRequiredArg().ofType(String.class);
		OptionSpec<Void> noInit = parser.accepts("no-init");
		OptionSpec<Void> noHttp = parser.accepts("no-http");
		OptionSpec<Integer> port = parser.accepts("port").withRequiredArg().ofType(Integer.class);
		OptionSpec<Void> traceChannels = parser.accepts("trace-channels");
		OptionSpec<String> files = parser.nonOptions().ofType(String.class);
		OptionSet optionSet  = parser.parse(argStrings);
		CommandLineOptions options = new CommandLineOptions();
		options.setHostname(optionSet.valueOf(hostname));
		options.setNoHttp(optionSet.has(noHttp));
		options.setInitScript(optionSet.valueOf(initFile));
		options.setNoInit(optionSet.has(noInit));
		options.setPort(optionSet.valueOf(port));
		options.setTraceChannels(optionSet.has(traceChannels));
		options.setScripts(optionSet.valuesOf(files));
		return options;
	}

}
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

import java.io.IOException;

@SpringBootApplication
public class AlerterApplication implements CommandLineRunner {

	public static final int DEFAULT_HTTP_PORT = 28018;

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
			if (options == null) {
				System.exit(1);
			}
			int port = (options.getPort() != null) ? options.getPort() : DEFAULT_HTTP_PORT;
			System.setProperty("server.port", Integer.toString(port));
			new SpringApplicationBuilder(AlerterApplication.class).web(!options.isNoHttp()).run(args);
		} catch (ConnectorStartFailedException e) {
			System.err.println("Cannot start the embedded http server on port " + e.getPort());
			System.err.println("To specify another port, use --port <port>");
			System.err.println("To start without an http server, use --no-http");
			System.exit(1);
		} catch (Throwable t) {
			System.err.println(t.getMessage());
			System.exit(1);
		}
	}

	private static CommandLineOptions parseOptions(String[] argStrings) throws IOException {
		OptionParser parser = new OptionParser();
		OptionSpec<String> hostname = parser.accepts("hostname", "specify the current machine's hostname").withRequiredArg().ofType(String.class);
		OptionSpec<String> initFile = parser.accepts("init", "load the given script at startup, instead of the default (startup.js)").withRequiredArg().ofType(String.class);
		OptionSpec<Void> noInit = parser.accepts("no-init", "do not load any initialization script at startup");
		OptionSpec<Void> noHttp = parser.accepts("no-http", "do not start the embedded http server");
		OptionSpec<Integer> port = parser.accepts("port","use the specified port for the embedded http server").withRequiredArg().ofType(Integer.class);
		OptionSpec<Void> traceChannels = parser.accepts("trace-channels","print sent messages to standard output, instead of sending them");
		OptionSpec<Void> help = parser.accepts("help","print this help").forHelp();
		OptionSpec<String> files = parser.nonOptions().ofType(String.class);
		OptionSet optionSet = parser.parse(argStrings);
		if (optionSet.has(help)) {
			parser.printHelpOn(System.out);
			return null;
		}
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
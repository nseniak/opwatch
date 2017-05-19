package org.opwatch;

import org.opwatch.common.ApplicationUtil;
import org.opwatch.service.ProcessorService;
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
public class Application implements CommandLineRunner {

	public static final int DEFAULT_HTTP_PORT = 28018;

	@Autowired
	private ProcessorService processorService;

	@Autowired
	private ApplicationContext applicationContext;

	private static CommandLineOptions options;

	@Override
	public void run(String... args) throws Exception {
		if (options == null) {
			throw new IllegalStateException("this method should only be called by main()");
		}
		boolean success = processorService.run(options);
		SpringApplication.exit(applicationContext, () -> success ? 0 : 1);
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
			new SpringApplicationBuilder(Application.class).web(!options.isNoServer()).run(args);
		} catch (ConnectorStartFailedException e) {
			System.err.println("Cannot start the embedded http server on port " + e.getPort());
			System.err.println("The port may already be in use. To specify another port, use --port <port>");
			System.err.println("To start without an http server, use --no-server");
			System.exit(1);
		} catch (Throwable t) {
			System.err.println("Could not start. An internal error occurred.");
			t.printStackTrace(System.err);
			System.exit(1);
		}
	}

	private static CommandLineOptions parseOptions(String[] argStrings) throws IOException {
		OptionParser parser = new OptionParser();
		OptionSpec<String> hostname = parser.accepts("hostname", "specify the current machine's hostname").withRequiredArg().ofType(String.class);
		OptionSpec<String> configFile = parser.accepts("config", "specify the filename or url of the configuration script, lo be loaded startup instead of the default one (config.js)").withRequiredArg().ofType(String.class);
		OptionSpec<Void> noConfig = parser.accepts("no-config", "do not load the configuration script at startup");
		OptionSpec<String> runExpression = parser.accepts("run", "evaluate the given Javascript expression to a processor, and run it").withRequiredArg().ofType(String.class);
		OptionSpec<Void> noServer = parser.accepts("no-server", "do not start the embedded http server");
		OptionSpec<Integer> port = parser.accepts("port","use the specified port for the embedded http server").withRequiredArg().ofType(Integer.class);
		OptionSpec<Void> traceChannels = parser.accepts("trace-channels","print sent messages to standard output, instead of sending them");
		OptionSpec<Void> help = parser.accepts("help","print this help").forHelp();
		OptionSpec<Void> version = parser.accepts("version","print the version and exit").forHelp();
		OptionSpec<String> files = parser.nonOptions("filenames or urls of scripts to execute in sequence").ofType(String.class);
		OptionSet optionSet = parser.parse(argStrings);
		if (optionSet.has(help)) {
			parser.printHelpOn(System.err);
			return null;
		}
		if (optionSet.has(version)) {
			System.out.println(Application.class.getPackage().getImplementationVersion());
			return null;
		}
		if (optionSet.has(runExpression) && !optionSet.valuesOf(files).isEmpty()) {
			System.err.println("Incompatible options: cannot pass both --run and script files to execute");
			return null;
		}
		CommandLineOptions options = new CommandLineOptions();
		options.setHostname(optionSet.valueOf(hostname));
		options.setNoServer(optionSet.has(noServer));
		options.setConfigScript(optionSet.valueOf(configFile));
		options.setNoConfig(optionSet.has(noConfig));
		options.setRunExpression(optionSet.valueOf(runExpression));
		options.setPort(optionSet.valueOf(port));
		options.setTraceChannels(optionSet.has(traceChannels));
		options.setScripts(optionSet.valuesOf(files));
		return options;
	}

}
package org.opwatch.testutil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static junit.framework.TestCase.assertEquals;

/**
 * Represents a test where the output of a produced is compared with expected output.
 */
abstract class OutputComparisonTest {

	public static Logger logger = LoggerFactory.getLogger(ResourceComparator.class);

	public enum DataType {

		text, html, json, png, other

	}

	private Class<?> loaderClass;
	private String expectedResourceName;
	private OutputComparator comparator;

	protected OutputComparisonTest(Class<?> loaderClass, String expectedResourceName) {
		this.expectedResourceName = expectedResourceName;
		this.loaderClass = loaderClass;
		DataType type = resourceType(expectedResourceName);
		OutputComparator comparator = dataComparator(type);
		this.comparator = comparator;
	}

	public abstract String output();

	public abstract String displayName();

	public void run() {
		run(false);
	}

	public void run(boolean overwrite) {
		Resource expectedResource = new ClassRelativeResourceLoader(loaderClass).getResource(expectedResourceName);
		if (overwrite || !expectedResource.exists()) {
			generate();
		} else {
			String displayName = displayName() + " => " + expectedResource.getFilename();
			logger.info("Running comparison: " + displayName);
			logger.info("   Expected output: " + expectedResource.getDescription());
			String expectedString = resourceString(expectedResource);
			comparator.compare(displayName, expectedString, output());
		}
	}

	public void generate() {
		try {
			URL outputResourceUrl = loaderClass.getResource(loaderClass.getSimpleName() + ".class");
			if (!(outputResourceUrl.getProtocol().equals("file"))) {
				throw new RuntimeException("Cannot generate resource missing file because resources are not in a filesystem");
			}
			String directory = new File(outputResourceUrl.getFile()).getParent().replace("target/test-classes", "src/test/resources").replace("target\\test-classes", "src\\test\\resources");
			File outputFile = new File(directory+"/"+expectedResourceName);
			logger.info("Generating output for test: " + displayName());
			logger.info("   Output to: " + outputFile);
			outputFile.getParentFile().mkdirs();
			FileWriter fw = new FileWriter(outputFile);
			fw.append(output());
			fw.close();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected String resourceString(Resource input) {
		try (InputStream stream = input.getInputStream()) {
			return new String(ByteStreams.toByteArray(stream), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("cannot load resource: " + input, e);
		}
	}

	public static DataType resourceType(String resourceName) {
		switch (Files.getFileExtension(resourceName)) {
			case "txt":
				return DataType.text;
			case "html":
				return DataType.html;
			case "json":
				return DataType.json;
			case "png":
				return DataType.png;
			default:
				return DataType.other;
		}
	}

	private static OutputComparator dataComparator(DataType type) {
		switch (type) {
			case text:
				return new TextComparator();
			case html:
				return new HtmlComparator();
			case json:
				return new JsonComparator();
			case png:
				return new PngComparator();
			default:
				return new TextComparator();
		}
	}

	public interface OutputComparator {

		void compare(String reason, String expected, String actual);

	}

	public static class JsonComparator implements OutputComparator {

		private ObjectMapper mapper = new ObjectMapper();

		@Override
		public void compare(String reason, String expected, String actual) {
			try {
				new TextComparator().compare(reason,
						jsonPrettyString(mapper.readValue(expected, Object.class)),
						jsonPrettyString(mapper.readValue(actual, Object.class)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private String jsonPrettyString(Object object) {
			try {
				return new ObjectMapper().writerWithDefaultPrettyPrinter()
						.writeValueAsString(object);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}

	}

	public static class TextComparator implements OutputComparator {

		@Override
		public void compare(String reason, String expected, String actual) {
			assertEquals(reason, expected, actual);
		}

	}

	public static class HtmlComparator implements OutputComparator {

		@Override
		public void compare(String reason, String expected, String actual) {
			// Cannot use Hamcrest assertThat() because erroneous output is not recognized by IntelliJ, which thus
			// doesn't add a "Click to see difference" link
			assertEquals(reason, pretty(expected), pretty(actual));
		}

		private String pretty(String html) {
			Document doc = Jsoup.parseBodyFragment(html);
			doc.outputSettings().prettyPrint(true);
			String prettyHtml = doc.body().html();
			// When pretty-printing, Jsoup puts spaces between > and <; remove them.
			return prettyHtml.replaceAll(">[ ]+<", "><").replaceAll("[ ]+\n", "\n").replaceAll("[ ]+$", "");
		}

	}

	public static class PngComparator implements OutputComparisonTest.OutputComparator {

		@Override
		public void compare(String reason, String expected, String actual) {
			assertEquals(reason, expected, actual);
		}

	}

}

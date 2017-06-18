/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.regex.Pattern;

@Service
public class ApplicationUtil {

	public static void checkEnvironmentVariable(String name) {
		if (System.getenv(name) == null) {
			throw new IllegalStateException("Environment variable not defined: " + name);
		}
	}

	public static String environmentVariable(String name) {
		checkEnvironmentVariable(name);
		return System.getenv(name);
	}

	public static <T> T  jsonEnvironmentVariable(String name, Class<T> clazz) throws IOException {
		String jsonString = environmentVariable(name);
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonString, clazz);
	}

	public static String environmentVariable(String name, String defaultValue) {
		String value = System.getenv(name);
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public static void checkProperty(String name, String value) {
		if (System.getProperty(name) == null) {
			throw new IllegalStateException("Missing Java parameter -D" + name + "=<" + value + ">");
		}
	}

	public static String property(String name, String defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public static int property(String name, int defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static long property(String name, long defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			return defaultValue;
		} else {
			return Long.parseLong(value);
		}
	}

	public static boolean property(String name, boolean defaultValue) {
		String value = System.getProperty(name);
		if (value == null) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(value);
		}
	}

	private static Pattern variablePattern = Pattern.compile("\\$\\{([A-Za-z0-9_\\.]+)\\}");

}

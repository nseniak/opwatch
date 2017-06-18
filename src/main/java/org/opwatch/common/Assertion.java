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
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;

public class Assertion {

	public static Logger logger = LoggerFactory.getLogger(Assertion.class);

	public static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
	}

	/**
	 * Verifies the given condition and displays an error if not true.
	 *
	 * @param condition
	 */
	public static void logExecutionStateError(boolean condition, Object... contextObjects) {
		if (!condition) {
			logger.error("Inconsistent execution state", new InconsistentExecutionStateException(contextObjects));
		}
	}

	/**
	 * Assertion that verifies that the execution state is consistent.
	 *
	 * @param condition
	 * @param message
	 */
	public static void assertExecutionState(String message, boolean condition, Object... contextObjects) {
		if (!condition) {
			throw new InconsistentExecutionStateException(message, contextObjects);
		}
	}

	/**
	 * Assertion that always fail. Use it when you are in a state where you shouldn't be
	 *
	 * @param message
	 */
	public static void fail(String message, Object... contextObjects) {
		throw new InconsistentExecutionStateException(message, contextObjects);
	}

	/**
	 * Assertion that verifies that the execution state is consistent.
	 *
	 * @param condition
	 */
	public static void assertExecutionState(boolean condition, Object... contextObjects) {
		if (!condition) {
			throw new InconsistentExecutionStateException(contextObjects);
		}
	}

	private static String jsonArrayLogString(Object[] objects) {
		StringJoiner joiner = new StringJoiner(", ", "[ ", " ]");
		for (Object object : objects) {
			joiner.add(jsonLogString(object));
		}
		return joiner.toString();
	}

	private static String jsonLogString(Object object) {
		try {
			if (object == null) {
				return "null";
			} else {
				return object.getClass().getSimpleName() + "@" + mapper.writeValueAsString(object);
			}
		} catch (Throwable t) {
			return object.getClass().getCanonicalName() + "@<cannot serialize object>";
		}
	}

	public static class InconsistentExecutionStateException extends IllegalStateException {

		Object[] contextObjects;

		public InconsistentExecutionStateException(String message) {
			super(message);
			this.contextObjects = new Object[0];
		}

		public InconsistentExecutionStateException(String message, Object[] contextObjects) {
			super(message + ": " + jsonArrayLogString(contextObjects));
			this.contextObjects = contextObjects;
		}

		public InconsistentExecutionStateException(Object[] contextObjects) {
			super(jsonArrayLogString(contextObjects));
			this.contextObjects = contextObjects;
		}

		public Object[] getContextObjects() {
			return contextObjects;
		}

		public void setContextObjects(Object[] contextObjects) {
			this.contextObjects = contextObjects;
		}

	}

}

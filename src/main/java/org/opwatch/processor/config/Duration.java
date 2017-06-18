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

package org.opwatch.processor.config;

import org.opwatch.processor.common.FactoryExecutionScope;
import org.opwatch.processor.common.ProcessorFactory;
import org.opwatch.processor.common.RuntimeError;

import java.time.format.DateTimeParseException;

public class Duration extends ConfigPropertyValue {

	public enum ValueType {
		milliseconds, text
	}

	private ValueType type;
	private long milliseconds;
	private String text;

	private Duration() {
	}

	public static Duration makeMilliseconds(long milliseconds) {
		Duration duration = new Duration();
		duration.type = ValueType.milliseconds;
		duration.milliseconds = milliseconds;
		return duration;
	}

	public static Duration makeText(String text) {
		Duration duration = new Duration();
		duration.type = ValueType.text;
		duration.text = text;
		return duration;
	}

	public long value(ProcessorFactory<?, ?> factory) {
		if (type == ValueType.milliseconds) {
			return milliseconds;
		}
		String duration;
		int start;
		if (text.startsWith("P") || text.startsWith("p")) {
			duration = text;
			start = 0;
		} else {
			duration = "pt" + text;
			start = 2;
		}
		try {
			return java.time.Duration.parse(duration).toMillis();
		} catch (DateTimeParseException e) {
			throw new RuntimeError(e.getLocalizedMessage() + " at index " + (e.getErrorIndex() - start) + ": \"" + text + "\"",
					new FactoryExecutionScope(factory), e);
		}
	}

	public long getMilliseconds() {
		return milliseconds;
	}

	public String getText() {
		return text;
	}

}

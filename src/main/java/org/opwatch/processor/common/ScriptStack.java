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

package org.opwatch.processor.common;

import jdk.nashorn.api.scripting.NashornException;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static org.opwatch.service.ScriptService.INIT_SCRIPT_PATH;

public class ScriptStack {

	private List<ScriptStackElement> elements = new ArrayList<>();

	private ScriptStack() {
	}

	private ScriptStack(Throwable t) {
		initFromStack(t.getStackTrace());
	}

	private ScriptStack(StackTraceElement[] stack) {
		initFromStack(stack);
	}

	private ScriptStack(Throwable t, String fileName, int lineNumber) {
		initFromStack(t.getStackTrace());
		if (fileName != null) {
			ScriptStack.ScriptStackElement top = top();
			if ((top == null) || !(fileName.equals(top.getFileName()) && (lineNumber == top.getLineNumber()))) {
				addElement(fileName, lineNumber);
			}
		}
	}

	public static ScriptStack exceptionStack(Throwable t) {
		Throwable current = t;
		while (current.getCause() != null) {
			if (current instanceof ScriptException) {
				ScriptException excep = (ScriptException) current;
				return new ScriptStack(excep, excep.getFileName(), excep.getLineNumber());
			} else if (current instanceof NashornException) {
				NashornException excep = (NashornException) current;
				return new ScriptStack(excep, excep.getFileName(), excep.getLineNumber());
			}
			current = current.getCause();
		}
		return new ScriptStack(current);
	}

	public static ScriptStack currentStack() {
		return new ScriptStack(Thread.currentThread().getStackTrace());
	}

	private void initFromStack(StackTraceElement[] javaStack) {
		for (StackTraceElement element : javaStack) {
			if (!element.getFileName().endsWith(".java")) {
				addElement(element.getFileName(), element.getLineNumber());
			}
		}
	}

	private void addElement(String fileName, int lineNumber) {
		if ((fileName != null) && !fileName.contains(INIT_SCRIPT_PATH)) {
			elements.add(ScriptStackElement.makeNew(fileName, lineNumber));
		}
	}

	private ScriptStackElement top() {
		if (elements.isEmpty()) {
			return null;
		} else {
			return elements.get(elements.size() - 1);
		}
	}

	public boolean empty() {
		return elements.isEmpty();
	}

	public String asString() {
		StringJoiner joiner = new StringJoiner("\n");
		for (ScriptStackElement element : elements) {
			joiner.add("at " + element.getFileName() + ":" + element.getLineNumber());
		}
		return joiner.toString();
	}

	public String asStringOrNull() {
		if (empty()) {
			return null;
		} else {
			return asString();
		}
	}

	public static class ScriptStackElement {

		private String fileName;
		private int lineNumber;

		private ScriptStackElement() {
		}

		public static ScriptStackElement makeNew(String fileName, int lineNumber) {
			ScriptStackElement element = new ScriptStackElement();
			element.fileName = fileName;
			element.lineNumber = lineNumber;
			return element;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public int getLineNumber() {
			return lineNumber;
		}

		public void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

	}

	public List<ScriptStackElement> getElements() {
		return elements;
	}

	public void setElements(List<ScriptStackElement> elements) {
		this.elements = elements;
	}

}

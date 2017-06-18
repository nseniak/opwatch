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

public class RuntimeError extends RuntimeException {

	public static Message.Level DEFAULT_ERROR_MESSAGE_LEVEL = Message.Level.emergency;

	private ExecutionScope scope;
	private Message.Level level;

	public RuntimeError(String message) {
		super(message);
		this.level = DEFAULT_ERROR_MESSAGE_LEVEL;
		this.scope = new GlobalExecutionScope();
	}

	public RuntimeError(String message, ExecutionScope scope) {
		super(message);
		this.level = DEFAULT_ERROR_MESSAGE_LEVEL;
		this.scope = scope;
	}


	public RuntimeError(String message, Throwable cause) {
		super(message, cause);
		this.level = DEFAULT_ERROR_MESSAGE_LEVEL;
		this.scope = new GlobalExecutionScope();
	}

	public RuntimeError(String message, ExecutionScope scope, Throwable cause) {
		super(message, cause);
		this.level = DEFAULT_ERROR_MESSAGE_LEVEL;
		this.scope = scope;
	}


	public RuntimeError(Throwable cause) {
		super(cause);
		this.level = DEFAULT_ERROR_MESSAGE_LEVEL;
		this.scope = new GlobalExecutionScope();
	}

	public RuntimeError(Throwable cause, ExecutionScope scope) {
		super(cause);
		this.level = DEFAULT_ERROR_MESSAGE_LEVEL;
		this.scope = scope;
	}

	public ExecutionScope getScope() {
		return scope;
	}

	public Message.Level getLevel() {
		return level;
	}

	public void setLevel(Message.Level level) {
		this.level = level;
	}

}

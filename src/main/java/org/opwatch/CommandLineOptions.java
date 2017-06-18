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

package org.opwatch;

import java.util.List;

public class CommandLineOptions {

	private String hostname;
	private boolean noServer;
	private String configScript;
	private boolean noConfig;
	private Integer port;
	private boolean traceChannels;
	private String runExpression;
	private List<String> scripts;

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public boolean isNoServer() {
		return noServer;
	}

	public void setNoServer(boolean noServer) {
		this.noServer = noServer;
	}

	public String getConfigScript() {
		return configScript;
	}

	public void setConfigScript(String configScript) {
		this.configScript = configScript;
	}

	public boolean isNoConfig() {
		return noConfig;
	}

	public void setNoConfig(boolean noConfig) {
		this.noConfig = noConfig;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public boolean isTraceChannels() {
		return traceChannels;
	}

	public void setTraceChannels(boolean traceChannels) {
		this.traceChannels = traceChannels;
	}

	public String getRunExpression() {
		return runExpression;
	}

	public void setRunExpression(String runExpression) {
		this.runExpression = runExpression;
	}

	public List<String> getScripts() {
		return scripts;
	}

	public void setScripts(List<String> scripts) {
		this.scripts = scripts;
	}

}

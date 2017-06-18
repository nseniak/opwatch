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

package org.opwatch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private ProcessorService processorService;

	@RequestMapping(value = "/stop", method = RequestMethod.POST)
	public void pause() {
		processorService.stopRunningProcessor();
	}

	@RequestMapping(value = "/trace/start", method = RequestMethod.POST)
	public void startTrace() {
		processorService.config().trace(true);
	}

	@RequestMapping(value = "/trace/stop", method = RequestMethod.POST)
	public void stopTrace() {
		processorService.config().trace(false);
	}

	@RequestMapping(value = "/exit", method = RequestMethod.POST)
	public void stop() {
		processorService.exit();
	}

	@RequestMapping("/healthcheck")
	public HealthcheckInfo healthcheck() {
		return processorService.healthcheck();
	}

}

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
public class HttpService {

	public static final String RECEIVE_PATH_PREFIX = "/receive/";

	private Multimap<String, PostBodyHandle> consumers = HashMultimap.create();

	public void addPostBodyConsumer(String path, PostBodyHandle consumer) {
		consumers.put(path, consumer);
	}

	public void removePostBodyConsumer(String urlPath, PostBodyHandle consumer) {
		consumers.remove(urlPath, consumer);
	}

	@RequestMapping(value = "/receive/**", method = RequestMethod.POST)
	public ResponseEntity<Void> receive(HttpServletRequest request, @RequestBody String body) throws JsonProcessingException {
		String urlPath = request.getServletPath().substring(RECEIVE_PATH_PREFIX.length());
		Collection<PostBodyHandle> pathConsumers = consumers.get(urlPath);
		if (pathConsumers.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		for (PostBodyHandle consumer : pathConsumers) {
			consumer.handlePost(body);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	public interface PostBodyHandle {

		void handlePost(String object);

	}

}

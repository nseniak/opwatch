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

	public static final String RECEIVE_PATH_PREFIX = "/receive";

	private Multimap<String, PostBodyHandle> consumers = HashMultimap.create();

	public void addPostBodyConsumer(String path, PostBodyHandle consumer) {
		consumers.put(path, consumer);
	}

	public void removePostBodyConsumer(String urlPath, PostBodyHandle consumer) {
		consumers.remove(urlPath, consumer);
	}

	@RequestMapping(value = "/receive/**", method = RequestMethod.POST)
	public ResponseEntity<Void> put(HttpServletRequest request, @RequestBody Object body) throws JsonProcessingException {
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

		void handlePost(Object object);

	}

}

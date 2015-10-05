package com.untrackr.alerter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private ProcessorService processorService;

	private Multimap<String, PostBodyConsumer> consumers = HashMultimap.create();

	public void addPostBodyConsumer(String path, PostBodyConsumer consumer) {
		consumers.put(path, consumer);
	}

	public void removePostBodyConsumer(String urlPath, PostBodyConsumer consumer) {
		consumers.remove(urlPath, consumer);
	}

	@RequestMapping(value = "/processor/**", method = RequestMethod.POST)
	public ResponseEntity<Void> put(HttpServletRequest request, @RequestBody Object body) throws JsonProcessingException {
		String urlPath = request.getServletPath().substring("/processor".length());
		Collection<PostBodyConsumer> pathConsumers = consumers.get(urlPath);
		if (pathConsumers.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		for (PostBodyConsumer consumer : pathConsumers) {
			consumer.consume(body);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	public interface PostBodyConsumer {

		void consume(Object object);

	}

}

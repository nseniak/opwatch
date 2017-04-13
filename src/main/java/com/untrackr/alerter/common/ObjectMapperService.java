package com.untrackr.alerter.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_NULL_MAP_VALUES;

@Service
public class ObjectMapperService {

	private ObjectMapper objectMapper;

	@PostConstruct
	public void initialize() throws Exception {
		// Object mapper
		objectMapper = new ObjectMapper();
		objectMapper.configure(WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(WRITE_NULL_MAP_VALUES, false);
		objectMapper.configure(ALLOW_COMMENTS, true);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public ObjectMapper objectMapper() {
		return objectMapper;
	}

}

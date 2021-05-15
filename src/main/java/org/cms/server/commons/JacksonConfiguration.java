package org.cms.server.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JacksonConfiguration {

	private final ObjectMapper mapper;

	public JacksonConfiguration() {
		mapper = new ObjectMapper();
	}

	public ObjectMapper getMapper() {
		return mapper;
	}
}

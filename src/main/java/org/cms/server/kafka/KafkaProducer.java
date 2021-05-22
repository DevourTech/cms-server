package org.cms.server.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cms.events.Event;
import org.cms.server.commons.JacksonConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final JacksonConfiguration jacksonConfiguration;

	public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, JacksonConfiguration jacksonConfiguration) {
		this.kafkaTemplate = kafkaTemplate;
		this.jacksonConfiguration = jacksonConfiguration;
	}

	public void produce(String topic, Event event, Object message) throws JsonProcessingException {
		String jsonMessage = jacksonConfiguration.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(message);
		logger.info(String.format("Producing message %s to topic %s", jsonMessage, topic));
		kafkaTemplate.send(topic, event.name(), jsonMessage);
	}
}

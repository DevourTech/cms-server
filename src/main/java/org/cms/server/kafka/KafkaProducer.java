package org.cms.server.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

	private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void produce(Object message, String topic) throws JsonProcessingException {
		String jsonMessage = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(message);
		logger.info(String.format("Producing message %s to topic %s", jsonMessage, topic));
		kafkaTemplate.send(topic, jsonMessage);
	}
}

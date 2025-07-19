package com.saimun.wallet.config;

import com.saimun.wallet.dto.TransactionRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

	@Bean
	public ProducerFactory<String, TransactionRequest> producerFactory() {
		Map<String, Object> configProps = new HashMap<>();
		configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
		configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class);
		configProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);
		configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
		configProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

		return new DefaultKafkaProducerFactory<>(configProps);
	}

	@Bean
	public KafkaTemplate<String, TransactionRequest> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}

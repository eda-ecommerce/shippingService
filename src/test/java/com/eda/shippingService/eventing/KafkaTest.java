package com.eda.shippingService.eventing;

import com.eda.shippingService.domain.entity.Product;
import com.eda.shippingService.domain.events.StockDecreasedEvent;
import com.eda.shippingService.infrastructure.eventing.EventPublisher;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.testcontainers.utility.DockerImageName;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Testcontainers
@SpringBootTest
@DirtiesContext
public class KafkaTest {
	@Container
	static final KafkaContainer kafkaContainer = new KafkaContainer(
        DockerImageName.parse("apache/kafka")
	);

	// For consuming
	CountDownLatch latch = new CountDownLatch(1);

	@Autowired
	EventPublisher kafkaEventPublisher;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;


	@DynamicPropertySource
	static void kafkaProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
	}

	@BeforeAll
    static void setUp() {
		kafkaContainer.start();
	}

	@BeforeEach void reset(){
		latch = new CountDownLatch(1);
	}

	@KafkaListener(topics = "stock")
	void listener(ConsumerRecord<String, String> record){

		System.out.println("---- Headers ----");
        for (Header header : record.headers()) {
			System.out.println("K: "+header.key() +" |V: "+ new String(header.value(), StandardCharsets.UTF_8));
        }
		System.out.println("---- Payload ----");
		System.out.println(record.value());
		System.out.println("-----------------");
		latch.countDown();
	}

	@Test
	void testKafkaEventPublisher() throws InterruptedException {
		StockDecreasedEvent event = new StockDecreasedEvent(
				null, new Product(UUID.randomUUID(), 10)
		);
		kafkaEventPublisher.publish(event, "stock");
		Assertions.assertTrue(latch.await(3, TimeUnit.SECONDS));
	}

}
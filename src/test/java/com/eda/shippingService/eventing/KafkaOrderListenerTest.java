package com.eda.shippingService.eventing;

import com.eda.shippingService.adapters.KafkaOrderListener;
import com.eda.shippingService.application.eventHandlers.OrderConfirmedEventHandler;
import com.eda.shippingService.application.eventHandlers.OrderRequestedEventHandler;
import com.eda.shippingService.domain.dto.incoming.OrderRequestedPayload;
import com.eda.shippingService.domain.events.OrderConfirmedEvent;
import com.eda.shippingService.domain.events.OrderRequestedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.eda.shippingService.TestHelpers.quickUUID;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class KafkaOrderListenerTest extends KafkaTest {
    @Autowired
    private KafkaOrderListener kafkaOrderListener;

    @MockBean
    private OrderConfirmedEventHandler orderConfirmedEventHandler;
    @MockBean
    private OrderRequestedEventHandler orderRequestedEventHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private KafkaTemplate<String,String > kafkaTemplate;

    @Test
    void shouldCallOrderConfirmedWithValidEvent() throws IOException, InterruptedException {
        Mockito.doNothing().when(orderConfirmedEventHandler).handle(Mockito.any());
        Mockito.doNothing().when(orderRequestedEventHandler).handle(Mockito.any());
        ArgumentCaptor<OrderConfirmedEvent> confirmedCaptor = ArgumentCaptor.forClass(OrderConfirmedEvent.class);
        ArgumentCaptor<OrderRequestedEvent> requestedCaptor = ArgumentCaptor.forClass(OrderRequestedEvent.class);
        String orderRequestedPayload = FileUtils.readFileToString(new File("src/test/java/com/eda/shippingService/eventing/data/requested.json"), StandardCharsets.UTF_8);
        log.info("Payload: {}", orderRequestedPayload);
        var record = new ProducerRecord<String, String>("order", orderRequestedPayload);
        record.headers().add("operation", "OrderRequested".getBytes(StandardCharsets.UTF_8));
        record.headers().add("messageId", quickUUID(111).toString().getBytes(StandardCharsets.UTF_8));
        //When
        kafkaTemplate.send(record);
        Thread.sleep(1000);
        //Then
        OrderRequestedEvent expected = new OrderRequestedEvent(null, quickUUID(111), 0,
                new OrderRequestedPayload(
                        quickUUID(123),
                        quickUUID(123456),
                        "20-12-24",
                        "InProcess",
                        List.of(
                                new OrderRequestedPayload.Product(quickUUID(456), 10)
                        )
                ));
        Mockito.verify(orderRequestedEventHandler).handle(requestedCaptor.capture());
        OrderRequestedEvent requestedEvent = requestedCaptor.getValue();
        assertEquals(requestedEvent.getMessageId(), quickUUID(111));
        assertEquals(requestedEvent.getPayload().products().get(0).quantity(), expected.getPayload().products().get(0).quantity());
    }
}

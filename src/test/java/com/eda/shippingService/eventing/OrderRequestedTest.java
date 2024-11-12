package com.eda.shippingService.eventing;


import com.eda.shippingService.TestHelpers;
import com.eda.shippingService.application.eventHandlers.OrderRequestedEventHandler;
import com.eda.shippingService.application.service.ShipmentService;
import com.eda.shippingService.domain.dto.incoming.OrderRequestedDTO;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.*;
import com.eda.shippingService.domain.events.OrderRequested;
import com.eda.shippingService.infrastructure.repo.IdempotentHandlerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.eda.shippingService.TestHelpers.quickAddress;
import static com.eda.shippingService.TestHelpers.quickUUID;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest()
public class OrderRequestedTest extends KafkaTest {
        @MockBean
        private ShipmentService shipmentService;

        @MockBean
        private IdempotentHandlerRepository idempotentHandlerRepository;

        @Autowired
        private OrderRequestedEventHandler orderRequestedEventHandler;

        private final ObjectMapper objectMapper = new ObjectMapper();

        //This is testing wrong. I'll have to fix it tomorrow
        //@Test
        public void shouldCallOrderConfirmedAndThrowValidDTO() throws InterruptedException, IOException {
                //Given
                UUID orderID = quickUUID(1);
                UUID messageId = quickUUID(2);
                UUID product1Id = quickUUID(3);
                UUID product2Id = quickUUID(4);
                Address dest = quickAddress("street");
                Address origin = quickAddress("street2");
                OrderRequestedDTO orderRequestedDTO = new OrderRequestedDTO(
                        orderID,
                        UUID.randomUUID(),
                        "2021-09-01",
                        "CONFIRMED",
                        List.of(
                                new OrderRequestedDTO.Product(product1Id, 1),
                                new OrderRequestedDTO.Product(product2Id, 5)
                        )
                );
                OrderRequested givenOrderRequested = new OrderRequested(null, messageId, System.currentTimeMillis(), orderRequestedDTO);

                Shipment givenShipment = new Shipment(
                        orderID,
                        dest,
                        origin,
                        null,
                        List.of(new OrderLineItem(product1Id, 1),
                                new OrderLineItem(product2Id, 5)),
                        ShipmentStatus.RESERVED);
                ShipmentDTO expectedShipmentRequestedPayload = new TestHelpers
                        .ShipmentDTOBuilder(orderID, "street", "street2", ShipmentStatus.RESERVED)
                        .withRequestedProduct(product1Id,1)
                        .withRequestedProduct(product2Id,5)
                        .build();

                //Mocks
                //Message has not been processed yet
                Mockito.when(idempotentHandlerRepository.findByMessageIdAndHandlerName(Mockito.any(UUID.class), Mockito.anyString())).thenReturn(Optional.empty());

                //Saving is possible
                Mockito.when(idempotentHandlerRepository.save(Mockito.any())).thenReturn(
                        new ProcessedMessage(messageId, OrderRequestedEventHandler.class.getSimpleName()
                        ));

                //Creating a Shipment Works
                Mockito.when(shipmentService.requestShipment(orderID,Mockito.any(ShipmentContentsDTO.class))).thenReturn(ShipmentDTO.fromEntity(givenShipment));

                //When
                orderRequestedEventHandler.handle(givenOrderRequested);

                //Then
                //The createShipment method should be called once
                Mockito.verify(shipmentService, Mockito.times(1)).requestShipment(orderID,Mockito.any());
                //An event is published
                Thread.sleep(1000);
                var record = super.getConsumedRecords().get(0);
                var actualShipmentDTO = objectMapper.readValue(record.value(), ShipmentDTO.class);
                Assertions.assertEquals(expectedShipmentRequestedPayload, actualShipmentDTO);
                var headers = record.headers().toArray();
                Assertions.assertTrue(Arrays.stream(headers).map(header -> new String(header.value()))
                        .anyMatch(value -> value.equals("ShipmentRequestedEvent")));
        }

        @Test
        public void shouldNotProcess() throws InterruptedException {
                //Given
                UUID orderID = UUID.fromString("00000000-0000-0000-0000-111111111111");
                UUID messageId = UUID.fromString("00000000-0000-0000-0000-000000000001");
                UUID product1Id = UUID.fromString("00000000-0000-0000-0000-000000000002");
                UUID product2Id = UUID.fromString("00000000-0000-0000-0000-000000000003");
                Address address1 = new Address("street", "city", "zip", "country", "DE");
                Address address2 = new Address("street2", "city", "zip", "country", "DE");
                Shipment requestedShipment = new Shipment(orderID, address1, address2, null,List.of(new OrderLineItem(product1Id, 1), new OrderLineItem(product2Id, 5)), ShipmentStatus.RESERVED);


                OrderRequestedDTO orderRequestedDTO = new OrderRequestedDTO(
                        orderID,
                        UUID.randomUUID(),
                        "2021-09-01",
                        "CONFIRMED",
                        List.of(
                                new OrderRequestedDTO.Product(product1Id, 1),
                                new OrderRequestedDTO.Product(product2Id, 5)
                        )
                );
                OrderRequested orderRequested = new OrderRequested(null, messageId, System.currentTimeMillis(), orderRequestedDTO);

                //Mocks
                //Message has already been processed
                Mockito
                        .when(idempotentHandlerRepository.findByMessageIdAndHandlerName(Mockito.any(UUID.class), Mockito.anyString()))
                        .thenReturn(Optional.of(new ProcessedMessage(messageId, OrderRequestedEventHandler.class.getSimpleName())));

                //Saving is possible
                Mockito.when(idempotentHandlerRepository.save(Mockito.any())).thenReturn(
                        new ProcessedMessage(messageId, OrderRequestedEventHandler.class.getSimpleName()
                        ));

                //Creating a Shipment Works
                Mockito.when(shipmentService.requestShipment(Mockito.any(),Mockito.any())).thenReturn(ShipmentDTO.fromEntity(requestedShipment));

                //When
                orderRequestedEventHandler.handle(orderRequested);

                //Then
                Mockito.verify(shipmentService, Mockito.times(0)).requestShipment(Mockito.any(),Mockito.any());
                Assertions.assertFalse(shipmentListenerLatch.await(1, TimeUnit.SECONDS));
        }
}

package com.eda.shippingService.eventing;

import com.eda.shippingService.application.eventHandlers.OrderConfirmedEventHandler;
import com.eda.shippingService.domain.dto.incoming.OrderConfirmedDTO;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.OrderConfirmedEvent;
import com.eda.shippingService.infrastructure.repo.IdempotentConsumerRepository;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static com.eda.shippingService.TestHelpers.quickAddress;
import static com.eda.shippingService.TestHelpers.quickUUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {OrderConfirmedEventHandler.class})
public class OrderConfirmedEventHandlerTest {
    @Autowired
    private OrderConfirmedEventHandler orderConfirmedEventHandler;

    @MockBean
    private ShipmentRepository shipmentRepository;
    @MockBean
    IdempotentConsumerRepository idempotentConsumerRepository;

    @Test
    public void shouldApproveShipment() {
        //Given
        Shipment shipment = new Shipment(
                quickUUID(1),
                quickAddress("Street1"),
                quickAddress("Street2"),
                null,
                List.of(
                        new OrderLineItem(quickUUID(2), 1)
                ),
                ShipmentStatus.RESERVED
        );
        OrderConfirmedEvent orderConfirmedEvent = new OrderConfirmedEvent(null, quickUUID(3), System.currentTimeMillis(),
                new OrderConfirmedDTO(quickUUID(1), quickUUID(99), "23-12-2021", "READY_FOR_SHIPMENT", List.of(
                        new OrderConfirmedDTO.Product(quickUUID(2), 1)
                )
                ));
        //Mocks
        Mockito.when(idempotentConsumerRepository.findByMessageIdAndHandlerName(Mockito.any(), Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(idempotentConsumerRepository.save(Mockito.any())).thenReturn(Mockito.mock(ProcessedMessage.class));
        Mockito.when(shipmentRepository.findById(Mockito.any())).thenReturn(Optional.of(shipment));
        Mockito.when(shipmentRepository.save(Mockito.any())).thenReturn(Mockito.mock(Shipment.class));

        //When
        orderConfirmedEventHandler.handle(orderConfirmedEvent);

        //Then
        Mockito.verify(shipmentRepository, Mockito.times(1)).findById(quickUUID(1));
        ArgumentCaptor<Shipment> shipmentCaptor = ArgumentCaptor.forClass(Shipment.class);
        Mockito.verify(shipmentRepository).save(shipmentCaptor.capture());
        Shipment savedShipment = shipmentCaptor.getValue();
        assertEquals(ShipmentStatus.APPROVED, savedShipment.getStatus());
    }
}

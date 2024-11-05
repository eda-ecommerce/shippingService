package com.eda.shippingService.eventing;

import com.eda.shippingService.application.eventHandlers.OrderRequestedEventHandler;
import com.eda.shippingService.domain.dto.incoming.OrderRequestedDTO;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.events.OrderRequestedEvent;
import com.eda.shippingService.infrastructure.repo.IdempotentConsumerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Slf4j
class IdempotentConsumerRepositoryTest {
    @Autowired
    private IdempotentConsumerRepository idempotentConsumerRepository;

    @Test
    void shouldFindEntry() {
        // Arrange
        UUID messageId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID orderId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        OrderRequestedDTO orderRequestedDTO = new OrderRequestedDTO(
            orderId,
            UUID.randomUUID(),
            "2021-09-01",
            "CONFIRMED",
            List.of(new OrderRequestedDTO.Product(UUID.randomUUID(), 1))
        );
        OrderRequestedEvent event = new OrderRequestedEvent(
                null, messageId, System.currentTimeMillis(), orderRequestedDTO
        );

        // Act
        idempotentConsumerRepository.save(new ProcessedMessage(event.getMessageId(), OrderRequestedEventHandler.class.getSimpleName()));

        // Assert
        assertTrue(idempotentConsumerRepository.findByMessageIdAndHandlerName(event.getMessageId(), OrderRequestedEventHandler.class.getSimpleName()).isPresent());
    }
}

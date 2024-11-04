package com.eda.shippingService.eventing;

import com.eda.shippingService.application.eventHandlers.OrderConfirmedEventHandler;
import com.eda.shippingService.domain.dto.incoming.OrderConfirmedPayload;
import com.eda.shippingService.domain.entity.ProcessedMessage;
import com.eda.shippingService.domain.events.OrderConfirmedEvent;
import com.eda.shippingService.infrastructure.repo.IdempotentConsumerRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("unit-test")
@Slf4j
public class IdempotentConsumerRepositoryTest {
    @Autowired
    private IdempotentConsumerRepository idempotentConsumerRepository;

    //@Test
    public void shouldFindEntry() {
        // Arrange
        UUID messageId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID orderId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        OrderConfirmedPayload orderConfirmedPayload = new OrderConfirmedPayload(
            orderId,
            UUID.randomUUID(),
            "2021-09-01",
            "CONFIRMED",
            List.of(new OrderConfirmedPayload.Product(UUID.randomUUID(), 1))
        );
        OrderConfirmedEvent event = new OrderConfirmedEvent(
                null, messageId, System.currentTimeMillis(), orderConfirmedPayload
        );

        // Act
        idempotentConsumerRepository.save(new ProcessedMessage(event.getMessageId(), OrderConfirmedEventHandler.class.getSimpleName()));

        // Assert
        assertTrue(idempotentConsumerRepository.findByMessageIdAndHandlerName(event.getMessageId(), OrderConfirmedEventHandler.class.getSimpleName()).isPresent());
    }
}

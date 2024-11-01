package com.eda.shippingService.infrastructure.repo;

import com.eda.shippingService.domain.entity.ProcessedMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdempotentConsumerRepository extends CrudRepository<ProcessedMessage, MessageCompositeKey> {
    Optional<ProcessedMessage> findByMessageIdAndHandlerName(UUID messageId, String handlerName);
}

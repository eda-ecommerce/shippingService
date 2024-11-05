package com.eda.shippingService.infrastructure.eventing;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "eda.kafka")
@Getter
@Setter
@Component
public class CustomKafkaProps {
    private String shipmentTopic;
    private String stockTopic;
    private String orderTopic;
}

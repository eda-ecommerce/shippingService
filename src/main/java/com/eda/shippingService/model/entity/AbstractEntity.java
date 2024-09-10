package com.eda.shippingService.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.UUID;

@Getter
@Setter
@Entity
public abstract class AbstractEntity {
    @Id
    private UUID id = UUID.randomUUID();
}

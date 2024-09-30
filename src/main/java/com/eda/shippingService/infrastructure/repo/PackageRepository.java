package com.eda.shippingService.infrastructure.repo;

import com.eda.shippingService.domain.entity.APackage;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PackageRepository extends CrudRepository<APackage, UUID> {
}

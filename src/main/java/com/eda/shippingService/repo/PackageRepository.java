package com.eda.shippingService.repo;

import com.eda.shippingService.model.entity.APackage;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PackageRepository extends CrudRepository<APackage, UUID> {
}

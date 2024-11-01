package com.eda.shippingService;

import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.dto.incoming.CreateShipmentRequestDTO;
import com.eda.shippingService.domain.dto.outgoing.AddressDTO;
import com.eda.shippingService.application.commandHandlers.CreateShipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.List;
import java.util.UUID;

@SpringBootApplication(scanBasePackages = "com.eda.shippingService")
@EnableKafka
@EntityScan("com.eda.shippingService.domain.entity")
public class ShippingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShippingServiceApplication.class, args);
	}

	//@Bean
	//@Autowired
	public CommandLineRunner loadData(CreateShipment createShipment) {

		return args -> {
			//Add stuff here
		};
	}
}

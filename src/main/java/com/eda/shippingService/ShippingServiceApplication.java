package com.eda.shippingService;

import com.eda.shippingService.application.commandHandlers.CreateShipment;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.kafka.annotation.EnableKafka;

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

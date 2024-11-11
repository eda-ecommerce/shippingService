package com.eda.shippingService.application.service;

import com.eda.shippingService.adapters.eventing.EventPublisher;
import com.eda.shippingService.application.service.exception.IncompleteContentException;
import com.eda.shippingService.application.service.exception.NotEnoughStockException;
import com.eda.shippingService.domain.dto.incoming.RequestShipmentDTO;
import com.eda.shippingService.domain.dto.incoming.UpdateShipmentStatusDTO;
import com.eda.shippingService.domain.dto.outgoing.AddressDTO;
import com.eda.shippingService.domain.dto.outgoing.PackageDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.ShipmentImpossible;
import com.eda.shippingService.domain.events.ShipmentRequested;
import com.eda.shippingService.domain.events.ShipmentReturned;
import com.eda.shippingService.domain.events.ShipmentSent;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ShipmentService  {
    private final ShipmentRepository shipmentRepository;
    private final EventPublisher eventPublisher;
    private final StockService stockService;
    @Value("kafka.topic.shipping")
    private String shipmentTopic;

    @Autowired
    public ShipmentService(ShipmentRepository shipmentRepository, EventPublisher eventPublisher, StockServiceImpl stockService) {
        this.shipmentRepository = shipmentRepository;
        this.eventPublisher = eventPublisher;
        this.stockService = stockService;
    }

    //it would be so easy to confuse destination and origin, should there be separate data types?
    public Shipment provideShippingAddresses(UUID orderId, AddressDTO destination, AddressDTO origin) {
        var found = shipmentRepository.findById(orderId);
        if (found.isPresent() && found.get().getStatus() == ShipmentStatus.REQUESTED) {
            var shipment = found.get();
            shipment.setDestination(destination.toEntity());
            shipment.setOrigin(origin.toEntity());
            return shipmentRepository.save(shipment);
        } else {
            throw new IllegalStateException("Order with id " +orderId+ " does not exist or is not in the correct state");
        }
    }

    public void approveShipment(UUID orderId){
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " +orderId+ " does not exist"));
        found.approve();
        shipmentRepository.save(found);
    }

    public Shipment requestShipment(RequestShipmentDTO shipmentDTO){
        if (shipmentDTO.orderId() == null || shipmentRepository.findByOrderId(shipmentDTO.orderId()) == null) {
            //Is this wanted behaviour? We could also just accept it and save over it?
            throw new IllegalArgumentException(String.format("Shipment with Order ID %s already exists.", shipmentDTO.orderId()));
        }
        Shipment shipmentEntity = shipmentDTO.toEntity();
        //Could be handled differently if we catch each reserve stock call and set status to partially complete or something / send out a smaller package
        try {
            for (var item: shipmentEntity.getRequestedProducts()) {
                stockService.reserveStock(item.productId(), item.quantity());
            }
            shipmentEntity.reserved();
            eventPublisher.publish(new ShipmentRequested(ShipmentDTO.fromEntity(shipmentEntity)), shipmentTopic);
            return shipmentRepository.save(shipmentEntity);
        } catch (NotEnoughStockException e){
            eventPublisher.publish(new ShipmentImpossible(ShipmentDTO.fromEntity(shipmentEntity)), shipmentTopic);
            shipmentEntity.setStatus(ShipmentStatus.ON_HOLD);
            return shipmentRepository.save(shipmentEntity);
        }
    }
    //Either like this
    public Shipment boxShipment(UUID orderId, PackageDTO packageDTO) throws IncompleteContentException {
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " +orderId+ " does not exist"));
        var aPackage = packageDTO.toEntity();
        if (aPackage.getContents().equals(found.getRequestedProducts())){
            found.addPackage(aPackage);
            return shipmentRepository.save(found);
        }
        throw new IncompleteContentException("Contents dont match requested products.y");
    }


    public void deleteShipment(UUID orderId){
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " +orderId+ " does not exist"));
        shipmentRepository.delete(found);
    }

    public void sendShipment(UUID orderId){
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " +orderId+ " does not exist"));
        if (found.checkContents()){
            //Notify external shipping company / driver and assign tracking number
            //This is obviously very mocked
            found.send();
            eventPublisher.publish(new ShipmentSent(found), shipmentTopic);
        }
    }

    public void externalShipmentStatusUpdate(UpdateShipmentStatusDTO dto){
        var found = shipmentRepository.findById(dto.orderId()).orElseThrow(() -> new IllegalStateException("Order with id " +dto.orderId()+ " does not exist"));
        switch (dto.externalShipmentStatus()){
            case SHIPPED -> {
                break;
            }
            case IN_DELIVERY -> {
                found.inDelivery();
                break;
            }
            case DELIVERED -> {
                found.delivered();
            }
            case FAILED -> {
                eventPublisher.publish(new ShipmentImpossible(ShipmentDTO.fromEntity(found)), shipmentTopic);
            }
            case RETURNED -> {
                eventPublisher.publish(new ShipmentReturned(ShipmentDTO.fromEntity(found)), shipmentTopic);
            }
        }
    }
}

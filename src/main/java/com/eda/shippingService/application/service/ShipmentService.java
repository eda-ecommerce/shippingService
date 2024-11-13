package com.eda.shippingService.application.service;

import com.eda.shippingService.adapters.eventing.EventPublisher;
import com.eda.shippingService.application.service.exception.IncompleteContentException;
import com.eda.shippingService.application.service.exception.NotEnoughStockException;
import com.eda.shippingService.domain.dto.incoming.IncomingPackageDTO;
import com.eda.shippingService.domain.dto.incoming.ShipmentContentsDTO;
import com.eda.shippingService.domain.dto.incoming.UpdateShipmentStatusDTO;
import com.eda.shippingService.domain.dto.common.AddressDTO;
import com.eda.shippingService.domain.dto.outgoing.ShipmentDTO;
import com.eda.shippingService.domain.entity.Address;
import com.eda.shippingService.domain.entity.OrderLineItem;
import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.domain.entity.ShipmentStatus;
import com.eda.shippingService.domain.events.*;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public Shipment provideShippingAddress(UUID orderId, AddressDTO destination) {
        var found = shipmentRepository.findById(orderId);
        if (found.isPresent() && found.get().getStatus() == ShipmentStatus.REQUESTED) {
            var shipment = found.get();
            shipment.setDestination(destination.toEntity());
            shipment.setOrigin(new Address("Our Street", "Our City", "NRW","50667","Germany"));
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

    public ShipmentDTO requestShipment(UUID orderId, ShipmentContentsDTO shipmentDTO){
        //TODO: Change RequestShipmentDTO to not include Shipping Addresses / Prevent Event From overwriting shipping address selected.
        Shipment shipmentEntity = shipmentDTO.toEntity(orderId);
        //Could be handled differently if we catch each reserve stock call and set status to partially complete or something / send out a smaller package
        try {
            for (var item: shipmentEntity.getRequestedProducts()) {
                stockService.reserveStock(item.productId(), item.quantity());
            }
            shipmentEntity.reserved();
            eventPublisher.publish(new ShipmentRequested(ShipmentDTO.fromEntity(shipmentEntity)), shipmentTopic);
            shipmentRepository.save(shipmentEntity);
            return ShipmentDTO.fromEntity(shipmentEntity);
        } catch (NotEnoughStockException e){
            shipmentEntity.setStatus(ShipmentStatus.ON_HOLD);
            // Should this be called shipmentImpossible ord on hold/needs intervention?
            eventPublisher.publish(new ShipmentImpossible(ShipmentDTO.fromEntity(shipmentEntity)), shipmentTopic);
            shipmentRepository.save(shipmentEntity);
            return ShipmentDTO.fromEntity(shipmentEntity);
        }
    }
    //This assumes that we call this with a complete package, maybe by an external working system. Idk if thats too much assumption
    //Otherwise call this with add content, but that would maybe justify a package service + reversal of dependencies for package
    public ShipmentDTO boxShipment(UUID orderId, IncomingPackageDTO packageDTO) throws IncompleteContentException {
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " +orderId+ " does not exist"));
        var aPackage = packageDTO.toPackage();
        for (OrderLineItem item: aPackage.getContents()) {
            stockService.releaseStock(item.productId(), item.quantity());
            stockService.adjustStock(item.productId(), -item.quantity());
        }
        if (aPackage.getContents().equals(found.getRequestedProducts())){
            found.addPackage(aPackage);
            shipmentRepository.save(found);
            eventPublisher.publish(new ShipmentBoxed(found), shipmentTopic);
            return ShipmentDTO.fromEntity(found);
        }
        throw new IncompleteContentException("Contents dont match requested products.");
    }


    public void deleteShipment(UUID orderId){
        var found = shipmentRepository.findById(orderId).orElseThrow(() -> new IllegalStateException("Order with id " +orderId+ " does not exist"));
        shipmentRepository.delete(found);
        switch (found.getStatus()){
            case RESERVED, APPROVED, PACKAGED: {
                for (var item: found.getRequestedProducts()) {
                    stockService.releaseStock(item.productId(), item.quantity());
                }
                break;
            }
        }
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

    public ShipmentDTO externalShipmentStatusUpdate(UpdateShipmentStatusDTO dto){
        var found = shipmentRepository.findById(dto.orderId()).orElseThrow(() -> new IllegalStateException("Order with id " +dto.orderId()+ " does not exist"));
        switch (dto.externalShipmentStatus()){
            case SHIPPED -> {}
            case IN_DELIVERY -> found.inDelivery();
            case DELIVERED -> {found.delivered();
                eventPublisher.publish(new ShipmentDelivered(dto.orderId(), ShipmentDTO.fromEntity(found)), shipmentTopic);
            }
            case FAILED -> eventPublisher.publish(new ShipmentImpossible(ShipmentDTO.fromEntity(found)), shipmentTopic);
            case RETURNED -> eventPublisher.publish(new ShipmentReturned(ShipmentDTO.fromEntity(found)), shipmentTopic);
            default -> throw new IllegalStateException("Unexpected value: " + dto.externalShipmentStatus());
        }
        shipmentRepository.save(found);
        return ShipmentDTO.fromEntity(found);
    }
}

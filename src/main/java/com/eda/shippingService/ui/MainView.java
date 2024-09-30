package com.eda.shippingService.ui;


import com.eda.shippingService.domain.entity.Shipment;
import com.eda.shippingService.infrastructure.repo.ShipmentRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;

@Route
public class MainView extends VerticalLayout {
    //Just a PoC
    private final ShipmentRepository shipmentRepository;

    @Autowired
    public MainView(
            ShipmentRepository shipmentRepository
    ) {
        this.shipmentRepository = shipmentRepository;
        add(new Button("Click me", e -> Notification.show(getOne().getOrigin().toString())));
    }
    public Shipment getOne(){
        Iterator<Shipment> iterator = shipmentRepository.findAll().iterator();
        if (iterator.hasNext()){
            return iterator.next();
        }
        else throw new NotFoundException();
    }
}

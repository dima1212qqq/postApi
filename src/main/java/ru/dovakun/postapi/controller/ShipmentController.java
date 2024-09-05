package ru.dovakun.postapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dovakun.postapi.enums.StatusShipment;
import ru.dovakun.postapi.model.Shipment;
import ru.dovakun.postapi.model.ShipmentHistory;
import ru.dovakun.postapi.model.PostOffice;
import ru.dovakun.postapi.service.PostOfficeService;
import ru.dovakun.postapi.service.ShipmentHistoryService;
import ru.dovakun.postapi.service.ShipmentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @Autowired
    private ShipmentHistoryService shipmentHistoryService;

    @Autowired
    private PostOfficeService postOfficeService;

    @PostMapping("/register")
    public ResponseEntity<Shipment> registerShipment(@RequestBody Shipment shipment) {
        shipment.setStatus(StatusShipment.Регистрация);
        Shipment createdShipment = shipmentService.register(shipment);
        createAndSaveShipmentHistory(createdShipment);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdShipment);
    }

    @PostMapping("/{id}/arrival/{postOfficeIndex}")
    public ResponseEntity<Shipment> arriveShipment(@PathVariable Long id, @PathVariable String postOfficeIndex) {
        Shipment shipment = shipmentService.findShipmentById(id);
        shipment.setStatus(StatusShipment.ПрибытиеВПромежуточноеОтделение);
        PostOffice postOffice = postOfficeService.findByIndex(postOfficeIndex);
        shipment.setCurrentPostOffice(postOffice);
        Shipment updatedShipment = shipmentService.register(shipment);
        createAndSaveShipmentHistory(updatedShipment, postOffice);
        return ResponseEntity.ok(updatedShipment);
    }

    @PostMapping("/{id}/departure")
    public ResponseEntity<Shipment> departShipment(@PathVariable Long id) {
        Shipment shipment = shipmentService.findShipmentById(id);
        shipment.setStatus(StatusShipment.УбытиеИзПромежуточногоОтделения);
        Shipment updatedShipment = shipmentService.register(shipment);
        createAndSaveShipmentHistory(updatedShipment);
        return ResponseEntity.ok(updatedShipment);
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<Shipment> deliverShipment(@PathVariable Long id) {
        Shipment shipment = shipmentService.findShipmentById(id);
        shipment.setStatus(StatusShipment.Выдана);
        Shipment updatedShipment = shipmentService.register(shipment);
        createAndSaveShipmentHistory(updatedShipment);
        return ResponseEntity.ok(updatedShipment);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Shipment> getShipmentStatus(@PathVariable Long id) {
        Shipment shipment = shipmentService.findShipmentById(id);
        return ResponseEntity.ok(shipment);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ShipmentHistory>> getShipmentHistory(@PathVariable Long id) {
        List<ShipmentHistory> history = shipmentHistoryService.findByShipmentId(id);
        return ResponseEntity.ok(history);
    }

    private void createAndSaveShipmentHistory(Shipment shipment) {
        createAndSaveShipmentHistory(shipment, shipment.getCurrentPostOffice());
    }

    private void createAndSaveShipmentHistory(Shipment shipment, PostOffice postOffice) {
        ShipmentHistory history = ShipmentHistory.builder()
                .shipmentId(shipment.getId())
                .currentPostOffice(postOffice)
                .timestamp(LocalDateTime.now())
                .status(shipment.getStatus())
                .build();
        shipmentHistoryService.save(history);
    }
}
package ru.dovakun.postapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dovakun.postapi.enums.StatusShipment;
import ru.dovakun.postapi.model.Shipment;
import ru.dovakun.postapi.model.ShipmentHistory;
import ru.dovakun.postapi.model.PostOffice;
import ru.dovakun.postapi.repo.ShipmentHistoryRepo;
import ru.dovakun.postapi.service.ShipmentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private ShipmentHistoryRepo shipmentHistoryRepo;

    @PostMapping("/register")
    public ResponseEntity<Shipment> registerShipment(@RequestBody Shipment shipment) {
        shipment.setStatus(StatusShipment.Регистрация);
        Shipment createdShipment = shipmentService.register(shipment);

        ShipmentHistory history = ShipmentHistory.builder()
                .shipmentId(createdShipment.getId())
                .currentPostOffice(createdShipment.getCurrentPostOffice())
                .timestamp(LocalDateTime.now())
                .status(createdShipment.getStatus())
                .build();
        shipmentHistoryRepo.save(history);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdShipment);
    }

    @PostMapping("/{id}/arrival/{postOfficeIndex}")
    public ResponseEntity<Shipment> arriveShipment(@RequestParam Long id, @RequestParam String postOfficeId) {
        Shipment shipment = shipmentService.findShipmentById(id);
        shipment.setStatus(StatusShipment.ПрибытиеВПромежуточноеОтделение);
        PostOffice postOffice = new PostOffice();
        postOffice.setIndex(postOfficeId);
        shipment.setCurrentPostOffice(postOffice);
        Shipment updatedShipment = shipmentService.register(shipment);

        ShipmentHistory history = ShipmentHistory.builder()
                .shipmentId(id)
                .currentPostOffice(postOffice)
                .timestamp(LocalDateTime.now())
                .status(updatedShipment.getStatus())
                .build();
        shipmentHistoryRepo.save(history);

        return ResponseEntity.ok(updatedShipment);
    }

    @PostMapping("/{id}/departure")
    public ResponseEntity<Shipment> departShipment(@RequestParam Long id) {
        Shipment shipment = shipmentService.findShipmentById(id);
        shipment.setStatus(StatusShipment.УбытиеИзПромежуточногоОтделения);
        Shipment updatedShipment = shipmentService.register(shipment);

        ShipmentHistory history = ShipmentHistory.builder()
                .shipmentId(id)
                .currentPostOffice(shipment.getCurrentPostOffice())
                .timestamp(LocalDateTime.now())
                .status(updatedShipment.getStatus())
                .build();
        shipmentHistoryRepo.save(history);

        return ResponseEntity.ok(updatedShipment);
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<Shipment> deliverShipment(@RequestParam Long id) {
        Shipment shipment = shipmentService.findShipmentById(id);
        shipment.setStatus(StatusShipment.Выдана);
        Shipment updatedShipment = shipmentService.register(shipment);

        ShipmentHistory history = ShipmentHistory.builder()
                .shipmentId(id)
                .currentPostOffice(updatedShipment.getCurrentPostOffice())
                .timestamp(LocalDateTime.now())
                .status(updatedShipment.getStatus())
                .build();
        shipmentHistoryRepo.save(history);

        return ResponseEntity.ok(updatedShipment);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Shipment> getShipmentStatus(@RequestParam Long id) {
        Shipment shipment = shipmentService.findShipmentById(id);
        return ResponseEntity.ok(shipment);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<ShipmentHistory>> getShipmentHistory(@RequestParam Long id) {
        List<ShipmentHistory> history = shipmentHistoryRepo.findByShipmentId(id);
        return ResponseEntity.ok(history);
    }
}
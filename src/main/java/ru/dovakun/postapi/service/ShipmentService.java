package ru.dovakun.postapi.service;

import org.springframework.stereotype.Service;
import ru.dovakun.postapi.enums.StatusShipment;
import ru.dovakun.postapi.model.PostOffice;
import ru.dovakun.postapi.model.Shipment;
import ru.dovakun.postapi.model.ShipmentHistory;
import ru.dovakun.postapi.repo.PostOfficeRepo;
import ru.dovakun.postapi.repo.ShipmentHistoryRepo;
import ru.dovakun.postapi.repo.ShipmentRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShipmentService {
    private final ShipmentRepo shipmentRepo;
    private final PostOfficeRepo postOfficeRepo;
    private final ShipmentHistoryRepo shipmentHistoryRepo;

    public Shipment findShipmentById(Long id) {
        return shipmentRepo.findById(id).orElseThrow(() -> new RuntimeException("Shipment not found"));
    }
    public List<Shipment> getShipments(){
        return shipmentRepo.findAll();
    }
    public void update(Shipment shipment){

    }
    public Shipment register(Shipment shipment){
        shipmentRepo.save(shipment);
        return shipment;
    }
    public ShipmentService(ShipmentRepo shipmentRepo, PostOfficeRepo postOfficeRepo, ShipmentHistoryRepo shipmentHistoryRepo) {
        this.shipmentRepo = shipmentRepo;
        this.postOfficeRepo = postOfficeRepo;
        this.shipmentHistoryRepo = shipmentHistoryRepo;
    }

    public void delete(Shipment shipment) {
        shipmentRepo.delete(shipment);
    }
}

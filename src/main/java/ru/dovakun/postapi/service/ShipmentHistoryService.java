package ru.dovakun.postapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.dovakun.postapi.model.Shipment;
import ru.dovakun.postapi.model.ShipmentHistory;
import ru.dovakun.postapi.repo.ShipmentHistoryRepo;

import java.util.List;

@Service
public class ShipmentHistoryService {
    @Autowired
    private ShipmentHistoryRepo shipmentHistoryRepo;

    public ShipmentHistory save(ShipmentHistory shipmentHistory){
        return shipmentHistoryRepo.save(shipmentHistory);
    }
    public List<ShipmentHistory> findByShipmentId(Long id){
        return shipmentHistoryRepo.findByShipmentId(id);
    }
}

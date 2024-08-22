package ru.dovakun.postapi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dovakun.postapi.model.ShipmentHistory;

import java.util.List;

public interface ShipmentHistoryRepo extends JpaRepository<ShipmentHistory, Long> {
    List<ShipmentHistory> findByShipmentId(Long shipmentId);
}

package ru.dovakun.postapi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.dovakun.postapi.model.Shipment;

public interface ShipmentRepo extends JpaRepository<Shipment, Long> {
}

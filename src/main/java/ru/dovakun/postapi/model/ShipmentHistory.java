package ru.dovakun.postapi.model;

import jakarta.persistence.*;
import lombok.*;
import ru.dovakun.postapi.enums.StatusShipment;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ShipmentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shipmentId;
    @Enumerated(EnumType.STRING)
    private StatusShipment status;
    @ManyToOne
    private PostOffice currentPostOffice;

    private LocalDateTime timestamp;
}

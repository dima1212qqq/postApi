package ru.dovakun.postapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import ru.dovakun.postapi.enums.StatusShipment;
import ru.dovakun.postapi.enums.TypeShipment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private TypeShipment type;
    private String index;
    private String address;
    @Enumerated(EnumType.STRING)
    private StatusShipment status;
    @ManyToOne
    @JoinColumn(name = "origin_post_office_id")
    private PostOffice originPostOffice;
    @ManyToOne
    @JoinColumn(name = "current_post_office_id")
    private PostOffice currentPostOffice;
    @ManyToOne
    @JoinColumn(name = "destination_post_office_id")
    private PostOffice destinationPostOffice;



}

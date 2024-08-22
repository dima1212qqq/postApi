package ru.dovakun.postapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PostOffice {
    @Id
    private String index;
    private String name;
    private String address;
    @Override
    public String toString() {
        return getName();
    }
}

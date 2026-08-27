package co.com.srdejo.plazoleta.infrastructure.out.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RestaurantEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "restaurant_id", nullable = false)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = 100)
    private String address;

    @Column(length = 50)
    private long ownerId;

    @Column(length = 50)
    private String phoneNumber;

    @Column(length = 50)
    private String urlLogo;

    @Column(length = 50)
    private String taxId;
}

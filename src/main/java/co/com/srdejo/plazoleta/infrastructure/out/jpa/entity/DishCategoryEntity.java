package co.com.srdejo.plazoleta.infrastructure.out.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dish_categories")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DishCategoryEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "dish_category_id", nullable = false)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    @OneToMany(mappedBy = "dishCategory")
    private List<DishEntity> dishes = new ArrayList<>();
}

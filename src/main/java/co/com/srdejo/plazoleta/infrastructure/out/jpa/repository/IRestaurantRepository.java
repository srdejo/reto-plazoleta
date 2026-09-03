package co.com.srdejo.plazoleta.infrastructure.out.jpa.repository;

import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRestaurantRepository extends JpaRepository<RestaurantEntity, Long> {

}

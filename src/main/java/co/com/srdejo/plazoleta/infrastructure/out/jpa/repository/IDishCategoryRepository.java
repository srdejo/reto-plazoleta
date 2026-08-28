package co.com.srdejo.plazoleta.infrastructure.out.jpa.repository;

import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.DishCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDishCategoryRepository extends JpaRepository<DishCategoryEntity, Long> {

}
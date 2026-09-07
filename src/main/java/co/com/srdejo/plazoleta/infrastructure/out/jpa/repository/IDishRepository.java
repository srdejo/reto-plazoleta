package co.com.srdejo.plazoleta.infrastructure.out.jpa.repository;

import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.DishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDishRepository extends JpaRepository<DishEntity, Long> {

    Page<DishEntity> findAllByDishCategory_Id(Long categoryId, Pageable pageable);
}
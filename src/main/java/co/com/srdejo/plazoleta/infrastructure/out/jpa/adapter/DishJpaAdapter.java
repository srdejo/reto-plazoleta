package co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter;

import co.com.srdejo.plazoleta.domain.model.DishModel;
import co.com.srdejo.plazoleta.domain.model.PageRequestModel;
import co.com.srdejo.plazoleta.domain.model.PageResultModel;
import co.com.srdejo.plazoleta.domain.spi.IDishPersistencePort;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.DishEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IDishEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IDishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class DishJpaAdapter implements IDishPersistencePort {

    private final IDishRepository dishRepository;
    private final IDishEntityMapper dishEntityMapper;


    @Override
    public DishModel saveDish(DishModel dishModel) {
        DishEntity dishEntity = dishRepository.save(dishEntityMapper.toEntity(dishModel));
        return dishEntityMapper.toDishModel(dishEntity);
    }

    @Override
    public DishModel findById(Long id) {
        DishEntity dishEntity = dishRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);
        return dishEntityMapper.toDishModel(dishEntity);
    }

    @Override
    public PageResultModel<DishModel> getAllDishes(Long categoryId, PageRequestModel pageRequestModel) {
        Sort sort = Sort.by(pageRequestModel.ascending() ? Sort.Direction.ASC : Sort.Direction.DESC, "name");
        Pageable pageable = PageRequest.of(pageRequestModel.page(), pageRequestModel.size(), sort);

        Page<DishEntity> entityPage = dishRepository.findAllByDishCategory_Id(categoryId, pageable);
        if (entityPage.isEmpty()) {
            throw new NoDataFoundException();
        }

        return new PageResultModel<>(
                dishEntityMapper.toDishModelList(entityPage.getContent()),
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages());
    }
}
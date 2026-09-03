package co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter;

import co.com.srdejo.plazoleta.domain.model.PageRequestModel;
import co.com.srdejo.plazoleta.domain.model.PageResultModel;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import co.com.srdejo.plazoleta.infrastructure.exception.NoDataFoundException;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.entity.RestaurantEntity;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import co.com.srdejo.plazoleta.infrastructure.out.jpa.repository.IRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantJpaAdapterTest {

    @Mock
    private IRestaurantRepository restaurantRepository;

    @Mock
    private IRestaurantEntityMapper restaurantEntityMapper;

    private RestaurantJpaAdapter restaurantJpaAdapter;

    @BeforeEach
    void setUp() {
        restaurantJpaAdapter = new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Test
    void getAllRestaurants_whenRepositoryEmpty_throwsNoDataFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RestaurantEntity> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(restaurantRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        PageRequestModel pageRequestModel = new PageRequestModel(0, 10, true);

        assertThatThrownBy(() -> restaurantJpaAdapter.getAllRestaurants(pageRequestModel))
                .isInstanceOf(NoDataFoundException.class);
    }

    @Test
    void getAllRestaurants_whenRepositoryHasData_returnsMappedPageResult() {
        RestaurantEntity entity = new RestaurantEntity();
        RestaurantModel model = new RestaurantModel(1L, "Pizzeria La Bella", "Cra 1 # 2-3", 10L,
                "+573005698325", "http://logo.png", "1234567890123");
        Pageable pageable = PageRequest.of(0, 10);
        Page<RestaurantEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);
        when(restaurantRepository.findAll(any(Pageable.class))).thenReturn(entityPage);
        when(restaurantEntityMapper.toRestaurantModelList(List.of(entity))).thenReturn(List.of(model));

        PageRequestModel pageRequestModel = new PageRequestModel(0, 10, true);
        PageResultModel<RestaurantModel> result = restaurantJpaAdapter.getAllRestaurants(pageRequestModel);

        assertThat(result.content()).containsExactly(model);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void saveRestaurant_mapsModelToEntitySavesAndMapsBack() {
        RestaurantModel model = new RestaurantModel(null, "Pizzeria La Bella", "Cra 1 # 2-3", 10L,
                "+573005698325", "http://logo.png", "1234567890123");
        RestaurantEntity entityToSave = new RestaurantEntity();
        RestaurantEntity savedEntity = new RestaurantEntity();
        RestaurantModel expectedModel = new RestaurantModel(1L, "Pizzeria La Bella", "Cra 1 # 2-3", 10L,
                "+573005698325", "http://logo.png", "1234567890123");
        when(restaurantEntityMapper.toEntity(model)).thenReturn(entityToSave);
        when(restaurantRepository.save(entityToSave)).thenReturn(savedEntity);
        when(restaurantEntityMapper.toRestaurantModel(savedEntity)).thenReturn(expectedModel);

        RestaurantModel result = restaurantJpaAdapter.saveRestaurant(model);

        assertThat(result).isEqualTo(expectedModel);
    }
}

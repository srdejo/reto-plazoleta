package co.com.srdejo.plazoleta.infrastructure.out.jpa.adapter;

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

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        when(restaurantRepository.findAll()).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> restaurantJpaAdapter.getAllRestaurants())
                .isInstanceOf(NoDataFoundException.class);
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

package co.com.srdejo.plazoleta.application.handler.impl;

import co.com.srdejo.plazoleta.application.dto.request.RestaurantRequestDto;
import co.com.srdejo.plazoleta.application.mapper.IRestaurantRequestMapper;
import co.com.srdejo.plazoleta.application.mapper.IRestaurantResponseMapper;
import co.com.srdejo.plazoleta.domain.api.IRestaurantServicePort;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.exception.InvalidOwnerException;
import co.com.srdejo.plazoleta.domain.model.RestaurantModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantHandlerTest {

    @Mock
    private IRestaurantServicePort restaurantServicePort;

    @Mock
    private IRestaurantRequestMapper restaurantRequestMapper;

    @Mock
    private IRestaurantResponseMapper restaurantResponseMapper;

    private RestaurantHandler restaurantHandler;

    @BeforeEach
    void setUp() {
        restaurantHandler = new RestaurantHandler(restaurantServicePort, restaurantRequestMapper, restaurantResponseMapper);
    }

    private RestaurantModel restaurantModel() {
        return new RestaurantModel(null, "Pizzeria La Bella", "Cra 1 # 2-3", 10L,
                "+573005698325", "http://logo.png", "1234567890123");
    }

    @Test
    void saveRestaurant_mapsDtoAndDelegatesToServicePort() {
        RestaurantRequestDto dto = new RestaurantRequestDto();
        RestaurantModel mappedModel = restaurantModel();
        when(restaurantRequestMapper.toRestaurant(dto)).thenReturn(mappedModel);

        restaurantHandler.saveRestaurant(dto);

        verify(restaurantServicePort).saveRestaurant(mappedModel);
    }

    @Test
    void saveRestaurant_propagatesInvalidOwnerException() {
        RestaurantRequestDto dto = new RestaurantRequestDto();
        RestaurantModel mappedModel = restaurantModel();
        when(restaurantRequestMapper.toRestaurant(dto)).thenReturn(mappedModel);
        doThrow(new InvalidOwnerException(ErrorCodesEnum.INVALID_OWNER_ID))
                .when(restaurantServicePort).saveRestaurant(mappedModel);

        assertThatThrownBy(() -> restaurantHandler.saveRestaurant(dto))
                .isInstanceOf(InvalidOwnerException.class);
    }
}

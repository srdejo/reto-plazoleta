package co.com.srdejo.plazoleta;

import co.com.srdejo.plazoleta.domain.spi.IOwnerClientPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-stack test: real Spring context, real H2 in-memory database through the JPA adapter,
 * and only the outbound Feign call to the owners service mocked. Verifies the restaurant is
 * actually persisted and round-tripped through the real repository/mapper chain, which the
 * unit tests (with mocked ports) cannot catch.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RestaurantEndToEndIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IOwnerClientPort ownerClientPort;

    @Test
    void saveRestaurant_thenGetAllRestaurants_returnsThePersistedRestaurant() throws Exception {
        when(ownerClientPort.existsOwner(10L)).thenReturn(true);

        String requestJson = """
                {
                  "name": "Pizzeria La Bella",
                  "address": "Cra 1 # 2-3",
                  "ownerId": 10,
                  "phoneNumber": "+573005698325",
                  "urlLogo": "http://logo.png",
                  "taxId": "1234567890123"
                }
                """;

        mockMvc.perform(post("/api/v1/restaurants/")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/v1/restaurants")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Pizzeria La Bella"))
                .andExpect(jsonPath("$.content[0].urlLogo").value("http://logo.png"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}

package co.com.srdejo.plazoleta.infrastructure.out.feign.client;

import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "UserClient", url = "${feign-url.customers}")
public interface UserClient {

    @GetMapping("/{id}")
    UserResponseDto getUserById(@PathVariable Long id);
}

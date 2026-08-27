package co.com.srdejo.plazoleta.infrastructure.out.feign.client;

import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "OwnerClient" , url = "${feign-url.owners}")
public interface OwnerClient {

    @GetMapping("/{id}")
    ResponseEntity<UserResponseDto> getOwnerById(@PathVariable Long id);
}

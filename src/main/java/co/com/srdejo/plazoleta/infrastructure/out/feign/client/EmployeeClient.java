package co.com.srdejo.plazoleta.infrastructure.out.feign.client;

import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.EmployeeResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "EmployeeClient", url = "${feign-url.employees}")
public interface EmployeeClient {

    @GetMapping("/me")
    EmployeeResponseDto getAuthenticatedEmployee();
}

package co.com.srdejo.plazoleta.infrastructure.out.feign.client;


import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.TraceabilityRequestDto;
import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.TraceabilityResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "TraceabilityClient", url = "${feign-url.traceability}")
public interface TraceabilityClient {

    @PostMapping
    ResponseEntity<TraceabilityResponseDto> registerStatusChange(@RequestBody TraceabilityRequestDto traceabilityRequestDto);
}

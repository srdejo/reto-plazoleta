package co.com.srdejo.plazoleta.infrastructure.out.feign.client;

import co.com.srdejo.plazoleta.infrastructure.out.feign.dto.SendSmsRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NotificationClient", url = "${feign-url.mensajeria}")
public interface NotificationClient {

    @PostMapping("/sms")
    void sendSms(@RequestBody SendSmsRequestDto sendSmsRequestDto);
}

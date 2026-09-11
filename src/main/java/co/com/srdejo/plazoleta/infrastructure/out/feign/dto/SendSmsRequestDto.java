package co.com.srdejo.plazoleta.infrastructure.out.feign.dto;

public record SendSmsRequestDto(String recipient, String message) {
}

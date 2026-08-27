package co.com.srdejo.plazoleta.infrastructure.out.feign.dto;

import java.time.LocalDate;

public record UserResponseDto(
        Long id,
        String name,
        String lastName,
        String document,
        String phone,
        LocalDate birthDate,
        String email,
        String role
) {
}

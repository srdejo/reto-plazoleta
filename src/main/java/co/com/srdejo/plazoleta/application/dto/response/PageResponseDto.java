package co.com.srdejo.plazoleta.application.dto.response;

import java.util.List;

public record PageResponseDto<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
}

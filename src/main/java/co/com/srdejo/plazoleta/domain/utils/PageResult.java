package co.com.srdejo.plazoleta.domain.utils;

import java.util.List;

public record PageResult<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
}

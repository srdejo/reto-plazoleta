package co.com.srdejo.plazoleta.domain.model;

import java.util.List;

public record PageResultModel<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
}

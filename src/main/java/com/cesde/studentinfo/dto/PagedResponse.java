package com.cesde.studentinfo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas
 * Envuelve los datos con metadatos de paginación
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean empty;
    private SortInfo sort;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortInfo {
        private boolean sorted;
        private String sortBy;
        private String direction;
    }

    /**
     * Crea una respuesta paginada desde un Page de Spring Data
     */
    public static <T> PagedResponse<T> from(Page<T> page) {
        SortInfo sortInfo;
        if (page.getSort().isSorted()) {
            var firstOrder = page.getSort().iterator().next();
            sortInfo = SortInfo.builder()
                    .sorted(true)
                    .sortBy(firstOrder.getProperty())
                    .direction(firstOrder.getDirection().name())
                    .build();
        } else {
            sortInfo = SortInfo.builder()
                    .sorted(false)
                    .build();
        }

        return PagedResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .sort(sortInfo)
                .build();
    }
}

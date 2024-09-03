package com.rey.modular.common.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.data.domain.Page;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PageInfo(
    Integer totalPages,
    Boolean hasNext,
    Boolean hasPrevious,
    Integer currentPage,
    Long totalElements
) {

    public static PageInfo from(Page page){
        if(page.getTotalPages() < 0) {
            return new PageInfo(null, null, null, null, null);
        }

        int currentPage = page.getNumber() + 1;
        if(currentPage <= page.getTotalPages()) {
            return new PageInfo(page.getTotalPages(), page.hasNext(), page.hasPrevious(), currentPage, page.getTotalElements());
        }
        else {
            return new PageInfo(page.getTotalPages(), null, null, null, page.getTotalElements());
        }
    }

}

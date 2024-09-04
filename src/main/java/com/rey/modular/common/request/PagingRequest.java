package com.rey.modular.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rey.modular.common.repository.BaseRepository.PageQueryOption;
import lombok.Getter;

import java.util.Optional;


public class PagingRequest extends SearchRequest {

    @JsonProperty("page_number")
    private Optional<Integer> pageNumber = Optional.empty();

    @JsonProperty("paging")
    private Optional<Boolean> pagingOptional = Optional.empty();

    @JsonProperty("page_size")
    @Getter
    private Optional<Integer> pageSizeOptional = Optional.empty();

    @JsonProperty("query_option")
    private Optional<String> queryOptionOptional = Optional.empty();

    public Integer getPageNumber() {
        return pageNumber.orElse(1);
    }

    public PageQueryOption getPageQueryOption() {
        boolean paging = pagingOptional.orElse(true);
        if (!paging) {
            return PageQueryOption.NO_COUNT;
        }
        return queryOptionOptional.map(PageQueryOption::of)
                .orElse(PageQueryOption.PAGE);
    }

}

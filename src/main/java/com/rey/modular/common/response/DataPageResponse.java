package com.rey.modular.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DataPageResponse<T>(
        List<T> data,
        @JsonProperty("page") PageInfo pageInfo
) {
}

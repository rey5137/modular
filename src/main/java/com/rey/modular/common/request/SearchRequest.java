package com.rey.modular.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
public class SearchRequest {

    @JsonProperty("response_fields")
    private Optional<List<String>> responseFieldsOptional = Optional.empty();

    @JsonProperty("orders")
    private Optional<List<Order>> ordersOptional = Optional.empty();

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Order {

        @JsonProperty("field")
        private String field;

        @JsonProperty("ascend")
        private Optional<Boolean> ascendOptional = Optional.empty();

        @JsonProperty("null_first")
        private Optional<Boolean> nullFirstOptional = Optional.empty();
    }

}

package com.rey.modular.common.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class SearchRequest {

    @JsonProperty("response_fields")
    private Optional<List<String>> responseFieldsOptional = Optional.empty();

}

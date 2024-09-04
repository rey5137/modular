package com.rey.modular.user.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rey.modular.common.request.PagingRequest;
import lombok.Getter;

import java.util.Optional;

@Getter
public class UserSearchRequest extends PagingRequest {

    @JsonProperty("id")
    private Optional<Integer> idOptional = Optional.empty();

    @JsonProperty("role_group_id")
    private Optional<Integer> roleGroupIdOptional = Optional.empty();


}
package com.rey.modular.user.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rey.modular.user.service.model.RoleGroup;

public record RoleGroupResponse(
        @JsonProperty(ID) Integer id,
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) String description
) {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";

    public RoleGroupResponse(RoleGroup roleGroup) {
        this(roleGroup.getId(), roleGroup.getName(), roleGroup.getDescription());
    }
}
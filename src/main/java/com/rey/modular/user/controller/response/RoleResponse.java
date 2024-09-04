package com.rey.modular.user.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rey.modular.user.service.model.Role;

public record RoleResponse(
        @JsonProperty(ID) Integer id,
        @JsonProperty(NAME) String name,
        @JsonProperty(DESCRIPTION) String description,
        @JsonProperty(ROLE_GROUP) RoleGroupResponse roleGroup
) {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String ROLE_GROUP = "role_group";

    public RoleResponse(Role role) {
        this(role.getId(), role.getName(), role.getDescription(), role.getRoleGroupOptional().map(RoleGroupResponse::new).orElse(null));
    }
}
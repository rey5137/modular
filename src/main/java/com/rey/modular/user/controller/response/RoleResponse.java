package com.rey.modular.user.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rey.modular.user.service.model.Role;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RoleResponse(
        Integer id,
        String name,
        String description,
        RoleGroupResponse roleGroup
) {
    public RoleResponse(Role role) {
        this(role.getId(), role.getName(), role.getDescription(), role.getRoleGroupOptional().map(RoleGroupResponse::new).orElse(null));
    }
}
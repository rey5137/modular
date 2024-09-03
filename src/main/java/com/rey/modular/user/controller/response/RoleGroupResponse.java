package com.rey.modular.user.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rey.modular.user.service.model.RoleGroup;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RoleGroupResponse(
        Integer id,
        String name,
        String description
) {
    public RoleGroupResponse(RoleGroup roleGroup) {
        this(roleGroup.getId(), roleGroup.getName(), roleGroup.getDescription());
    }
}
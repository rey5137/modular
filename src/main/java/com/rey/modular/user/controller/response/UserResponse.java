package com.rey.modular.user.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.rey.modular.user.service.model.User;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record UserResponse(
        Integer id,
        String name,
        String email,
        RoleResponse role
) {

    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRoleOptional().map(RoleResponse::new).orElse(null));
    }
}
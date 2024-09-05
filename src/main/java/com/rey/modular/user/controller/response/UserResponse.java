package com.rey.modular.user.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rey.modular.user.service.model.User;

public record UserResponse(
        @JsonProperty(ID) Integer id,
        @JsonProperty(NAME) String name,
        @JsonProperty(EMAIL) String email,
        @JsonProperty(ROLE) RoleResponse role
) {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String ROLE = "role.";

    public UserResponse(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getRoleOptional().map(RoleResponse::new).orElse(null));
    }
}
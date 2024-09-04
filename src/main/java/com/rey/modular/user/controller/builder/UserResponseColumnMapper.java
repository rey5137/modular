package com.rey.modular.user.controller.builder;

import com.rey.modular.common.repository.model.Column;
import com.rey.modular.user.controller.response.RoleGroupResponse;
import com.rey.modular.user.controller.response.RoleResponse;
import com.rey.modular.user.controller.response.UserResponse;
import com.rey.modular.user.service.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rey.modular.common.util.CollectionUtil.combineSet;

@Component
public class UserResponseColumnMapper {

    public static final Set<Column<User, ?, ?>> ALL_COLUMNS = combineSet(
            User.USER_COLUMNS,
            User.ROLE_COLUMNS,
            User.ROLE_GROUP_COLUMNS
    );

    public Set<Column<User, ?, ?>> getUserColumns(List<String> fields) {
        return fields.stream()
                .flatMap(this::getUserColumns)
                .collect(Collectors.toSet());
    }

    private Stream<Column<User, ?, ?>> getUserColumns(String field) {
        return switch (field) {
            case UserResponse.ID -> Stream.of(User.ID);
            case UserResponse.NAME -> Stream.of(User.NAME);
            case UserResponse.EMAIL -> Stream.of(User.EMAIL);
            default -> {
                if(field.startsWith(UserResponse.ROLE + "."))
                    yield getRoleColumns(field.substring(UserResponse.ROLE.length() + 1));
                yield Stream.empty();
            }
        };
    }

    private Stream<Column<User, ?, ?>> getRoleColumns(String field) {
        return switch (field) {
            case RoleResponse.ID -> Stream.of(User.ROLE_TABLE_ID);
            case RoleResponse.NAME -> Stream.of(User.ROLE_TABLE_NAME);
            case RoleResponse.DESCRIPTION -> Stream.of(User.ROLE_TABLE_DESCRIPTION);
            default -> {
                if(field.startsWith(RoleResponse.ROLE_GROUP + "."))
                    yield getRoleGroupColumns(field.substring(RoleResponse.ROLE_GROUP.length() + 1));
                yield Stream.empty();
            }
        };
    }

    private Stream<Column<User, ?, ?>> getRoleGroupColumns(String field) {
        return switch (field) {
            case RoleGroupResponse.ID -> Stream.of(User.ROLE_GROUP_TABLE_ID);
            case RoleGroupResponse.NAME -> Stream.of(User.ROLE_GROUP_TABLE_NAME);
            case RoleGroupResponse.DESCRIPTION -> Stream.of(User.ROLE_GROUP_TABLE_DESCRIPTION);
            default -> Stream.empty();
        };
    }
}

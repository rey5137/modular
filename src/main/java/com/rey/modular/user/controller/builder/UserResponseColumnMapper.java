package com.rey.modular.user.controller.builder;

import com.rey.modular.common.repository.model.Column;
import com.rey.modular.common.repository.model.OrderColumn;
import com.rey.modular.common.request.SearchRequest.Order;
import com.rey.modular.user.controller.response.RoleGroupResponse;
import com.rey.modular.user.controller.response.RoleResponse;
import com.rey.modular.user.controller.response.UserResponse;
import com.rey.modular.user.service.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rey.modular.common.util.CollectionUtil.combineSet;

@Component
public class UserResponseColumnMapper {

    public static final Collection<Column<User, ?, ?>> ALL_COLUMNS = combineSet(
            User.USER_COLUMNS,
            User.ROLE_COLUMNS,
            User.ROLE_GROUP_COLUMNS
    );

    public static final Collection<OrderColumn<User, ?, ?>> DEFAULT_ORDERS = List.of(
            User.ID.desc()
    );

    public Collection<Column<User, ?, ?>> getUserColumns(List<String> fields) {
        return fields.stream()
                .flatMap(this::getUserColumns)
                .collect(Collectors.toSet());
    }

    public Collection<OrderColumn<User, ?, ?>> getUserOrderColumns(List<Order> orders) {
        var linkedMap = new LinkedHashMap<Column<User, ?, ?>, Order>();
        for (var order : orders) {
            getUserColumns(order.getField()).forEach(column -> linkedMap.putIfAbsent(column, order));
        }
        if(!linkedMap.containsKey(User.ID)) {
            linkedMap.put(User.ID, new Order(null, Optional.of(false), Optional.of(false)));
        }
        List<OrderColumn<User, ?, ?>> orderColumns = new ArrayList<>();
        linkedMap.forEach((key, value) -> orderColumns.add(new OrderColumn<>(key, value.getAscendOptional().orElse(true), value.getNullFirstOptional().orElse(false))));
        return orderColumns;
    }

    private Stream<Column<User, ?, ?>> getUserColumns(String field) {
        return switch (field) {
            case UserResponse.ID -> Stream.of(User.ID);
            case UserResponse.NAME -> Stream.of(User.NAME);
            case UserResponse.EMAIL -> Stream.of(User.EMAIL);
            default -> {
                if(field.startsWith(UserResponse.ROLE))
                    yield getRoleColumns(field.substring(UserResponse.ROLE.length()));
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
                if(field.startsWith(RoleResponse.ROLE_GROUP))
                    yield getRoleGroupColumns(field.substring(RoleResponse.ROLE_GROUP.length()));
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

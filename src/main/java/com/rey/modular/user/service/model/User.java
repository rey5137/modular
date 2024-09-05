package com.rey.modular.user.service.model;

import com.rey.modular.common.repository.model.Column;
import com.rey.modular.common.repository.model.EntityModel;
import com.rey.modular.common.repository.model.IntegerColumn;
import com.rey.modular.common.repository.model.ModelQueryBuilder;
import com.rey.modular.common.repository.model.StringColumn;
import com.rey.modular.common.repository.model.Table;
import com.rey.modular.user.repository.entity.QRoleEntity;
import com.rey.modular.user.repository.entity.QRoleGroupEntity;
import com.rey.modular.user.repository.entity.QUserEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
public class User extends EntityModel<Integer, User> {

    public static final Table<QUserEntity, QUserEntity> USER_TABLE = Table.root(() -> new QUserEntity("user"));
    public static final Table<QUserEntity, QRoleEntity> ROLE_TABLE = USER_TABLE.leftJoin(() -> new QRoleEntity("role"), (user, role) -> user.roleId.eq(role.id));
    public static final Table<QRoleEntity, QRoleGroupEntity> ROLE_GROUP_TABLE = Role.ROLE_GROUP_TABLE.withParent(ROLE_TABLE);

    public static final IntegerColumn<User, QUserEntity> ID = USER_TABLE.integerCol(user -> user.id, User::setId);
    public static final StringColumn<User, QUserEntity> NAME = USER_TABLE.stringCol(user -> user.name, User::setName);
    public static final StringColumn<User, QUserEntity> EMAIL = USER_TABLE.stringCol(user -> user.email, User::setEmail);

    public static final IntegerColumn<User, QRoleEntity> ROLE_TABLE_ID = Role.ID.withTable(ROLE_TABLE, User::getOrCreateRole);
    public static final StringColumn<User, QRoleEntity> ROLE_TABLE_NAME = Role.NAME.withTable(ROLE_TABLE, User::getOrCreateRole);
    public static final StringColumn<User, QRoleEntity> ROLE_TABLE_DESCRIPTION = Role.DESCRIPTION.withTable(ROLE_TABLE, User::getOrCreateRole);

    public static final IntegerColumn<User, QRoleGroupEntity> ROLE_GROUP_TABLE_ID = Role.ROLE_GROUP_TABLE_ID.withTable(ROLE_GROUP_TABLE, User::getOrCreateRole);
    public static final StringColumn<User, QRoleGroupEntity> ROLE_GROUP_TABLE_NAME = Role.ROLE_GROUP_TABLE_NAME.withTable(ROLE_GROUP_TABLE, User::getOrCreateRole);
    public static final StringColumn<User, QRoleGroupEntity> ROLE_GROUP_TABLE_DESCRIPTION = Role.ROLE_GROUP_TABLE_DESCRIPTION.withTable(ROLE_GROUP_TABLE, User::getOrCreateRole);

    public static final List<Column<User, ?, ?>> USER_COLUMNS = List.of(ID, NAME, EMAIL);
    public static final List<Column<User, ?, ?>> ROLE_COLUMNS = List.of(ROLE_TABLE_ID, ROLE_TABLE_NAME, ROLE_TABLE_DESCRIPTION);
    public static final List<Column<User, ?, ?>> ROLE_GROUP_COLUMNS = List.of(ROLE_GROUP_TABLE_ID, ROLE_GROUP_TABLE_NAME, ROLE_GROUP_TABLE_DESCRIPTION);

    private Integer id;
    private String name;
    private String email;

    private Optional<Role> roleOptional = Optional.empty();

    @Override
    public Integer getPrimaryKey() {
        return id;
    }

    private Role getOrCreateRole() {
        if(roleOptional.isEmpty()) {
            roleOptional = Optional.of(new Role());
        }
        return roleOptional.get();
    }

    public static class QueryBuilder extends ModelQueryBuilder<QUserEntity, Integer, User> {

        public QueryBuilder(Collection<Column<User, ?, ?>> columns) {
            super(USER_TABLE, List.of(ID), columns, User::new, offset -> true);
        }
    }

}

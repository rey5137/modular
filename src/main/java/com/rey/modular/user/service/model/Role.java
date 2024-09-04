package com.rey.modular.user.service.model;

import com.rey.modular.common.repository.model.Column;
import com.rey.modular.common.repository.model.EntityModel;
import com.rey.modular.common.repository.model.IntegerColumn;
import com.rey.modular.common.repository.model.ModelQueryBuilder;
import com.rey.modular.common.repository.model.StringColumn;
import com.rey.modular.common.repository.model.Table;
import com.rey.modular.user.repository.entity.QRoleEntity;
import com.rey.modular.user.repository.entity.QRoleGroupEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Optional;

@Getter
@Setter
public class Role extends EntityModel<Integer, Role> {

    public static final Table<QRoleEntity, QRoleEntity> ROLE_TABLE = Table.root(() -> new QRoleEntity("role"));
    public static final Table<QRoleEntity, QRoleGroupEntity> ROLE_GROUP_TABLE = ROLE_TABLE.leftJoin(() -> new QRoleGroupEntity("role_group"), (role, roleGroup) -> role.roleGroupId.eq(roleGroup.id));

    public static final IntegerColumn<Role, QRoleEntity> ID = ROLE_TABLE.integerCol(role -> role.id, Role::setId);
    public static final StringColumn<Role, QRoleEntity> NAME = ROLE_TABLE.stringCol(role -> role.name, Role::setName);
    public static final StringColumn<Role, QRoleEntity> DESCRIPTION = ROLE_TABLE.stringCol(role -> role.description, Role::setDescription);

    public static final IntegerColumn<Role, QRoleGroupEntity> ROLE_GROUP_TABLE_ID = RoleGroup.ID.withTable(ROLE_GROUP_TABLE, Role::getOrCreateRoleGroup);
    public static final StringColumn<Role, QRoleGroupEntity> ROLE_GROUP_TABLE_NAME = RoleGroup.NAME.withTable(ROLE_GROUP_TABLE, Role::getOrCreateRoleGroup);
    public static final StringColumn<Role, QRoleGroupEntity> ROLE_GROUP_TABLE_DESCRIPTION = RoleGroup.DESCRIPTION.withTable(ROLE_GROUP_TABLE, Role::getOrCreateRoleGroup);

    private Integer id;
    private String name;
    private String description;

    private Optional<RoleGroup> roleGroupOptional = Optional.empty();

    @Override
    public Integer getPrimaryKey() {
        return id;
    }

    private RoleGroup getOrCreateRoleGroup() {
        if (roleGroupOptional.isEmpty()) {
            roleGroupOptional = Optional.of(new RoleGroup());
        }
        return roleGroupOptional.get();
    }

    public static class QueryBuilder extends ModelQueryBuilder<QRoleEntity, Integer, Role> {

        public QueryBuilder(Collection<Column<Role, ?, ?>> columns) {
            super(ROLE_TABLE, columns, Role::new);
        }
    }

}

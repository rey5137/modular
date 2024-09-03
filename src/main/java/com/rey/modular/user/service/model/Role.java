package com.rey.modular.user.service.model;

import com.querydsl.core.JoinType;
import com.rey.modular.common.repository.ColumnField;
import com.rey.modular.common.repository.EntityModel;
import com.rey.modular.common.repository.ModelQueryBuilder;
import com.rey.modular.common.repository.TableField;
import com.rey.modular.user.repository.entity.QRoleEntity;
import com.rey.modular.user.repository.entity.QRoleGroupEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Optional;

@Getter
@Setter
public class Role extends EntityModel<Integer, Role> {

    public static final TableField<QRoleEntity, QRoleEntity> ROLE_TABLE = TableField.root(QRoleEntity.roleEntity);
    public static final TableField<QRoleEntity, QRoleGroupEntity> ROLE_GROUP_TABLE = ROLE_TABLE.joinTable(JoinType.LEFTJOIN, QRoleGroupEntity.roleGroupEntity, (roleEntity, roleGroupEntity) -> roleEntity.roleGroupId.eq(roleGroupEntity.id));

    public static final ColumnField<Role, QRoleEntity, Integer> ID = ROLE_TABLE.column(qUserEntity -> qUserEntity.id, Role::setId);
    public static final ColumnField<Role, QRoleEntity, String> NAME = ROLE_TABLE.column(qUserEntity -> qUserEntity.name, Role::setName);
    public static final ColumnField<Role, QRoleEntity, String> DESCRIPTION = ROLE_TABLE.column(qUserEntity -> qUserEntity.description, Role::setDescription);

    public static final ColumnField<Role, QRoleGroupEntity, Integer> ROLE_GROUP_TABLE_ID = RoleGroup.ID.withTable(ROLE_GROUP_TABLE, Role::getOrCreateRoleGroup);
    public static final ColumnField<Role, QRoleGroupEntity, String> ROLE_GROUP_TABLE_NAME = RoleGroup.NAME.withTable(ROLE_GROUP_TABLE, Role::getOrCreateRoleGroup);
    public static final ColumnField<Role, QRoleGroupEntity, String> ROLE_GROUP_TABLE_DESCRIPTION = RoleGroup.DESCRIPTION.withTable(ROLE_GROUP_TABLE, Role::getOrCreateRoleGroup);

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

        public QueryBuilder(Collection<ColumnField<Role, ?, ?>> columnFields) {
            super(ROLE_TABLE, columnFields, Role::new);
        }
    }

}

package com.rey.modular.user.service.model;

import com.rey.modular.common.repository.ColumnField;
import com.rey.modular.common.repository.EntityModel;
import com.rey.modular.common.repository.ModelQueryBuilder;
import com.rey.modular.common.repository.TableField;
import com.rey.modular.user.repository.entity.QRoleGroupEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class RoleGroup extends EntityModel<Integer, RoleGroup> {

    public static final TableField<QRoleGroupEntity, QRoleGroupEntity> ROLE_GROUP_TABLE = TableField.root(QRoleGroupEntity.roleGroupEntity);

    public static final ColumnField<RoleGroup, QRoleGroupEntity, Integer> ID = ROLE_GROUP_TABLE.column(qUserEntity -> qUserEntity.id, RoleGroup::setId);
    public static final ColumnField<RoleGroup, QRoleGroupEntity, String> NAME = ROLE_GROUP_TABLE.column(qUserEntity -> qUserEntity.name, RoleGroup::setName);
    public static final ColumnField<RoleGroup, QRoleGroupEntity, String> DESCRIPTION = ROLE_GROUP_TABLE.column(qUserEntity -> qUserEntity.description, RoleGroup::setDescription);

    private Integer id;
    private String name;
    private String description;

    @Override
    public Integer getPrimaryKey() {
        return id;
    }

    public static class QueryBuilder extends ModelQueryBuilder<QRoleGroupEntity, Integer, RoleGroup> {

        public QueryBuilder(Collection<ColumnField<RoleGroup, ?, ?>> columnFields) {
            super(ROLE_GROUP_TABLE, columnFields, RoleGroup::new);
        }
    }

}

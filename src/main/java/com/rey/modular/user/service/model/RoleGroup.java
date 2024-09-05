package com.rey.modular.user.service.model;

import com.rey.modular.common.repository.model.EntityModel;
import com.rey.modular.common.repository.model.IntegerColumn;
import com.rey.modular.common.repository.model.StringColumn;
import com.rey.modular.common.repository.model.Table;
import com.rey.modular.user.repository.entity.QRoleGroupEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleGroup extends EntityModel<Integer, RoleGroup> {

    public static final Table<QRoleGroupEntity, QRoleGroupEntity> ROLE_GROUP_TABLE = Table.root(() -> new QRoleGroupEntity("role_group"));

    public static final IntegerColumn<RoleGroup, QRoleGroupEntity> ID = ROLE_GROUP_TABLE.integerCol(roleGroup -> roleGroup.id, RoleGroup::setId);
    public static final StringColumn<RoleGroup, QRoleGroupEntity> NAME = ROLE_GROUP_TABLE.stringCol(roleGroup -> roleGroup.name, RoleGroup::setName);
    public static final StringColumn<RoleGroup, QRoleGroupEntity> DESCRIPTION = ROLE_GROUP_TABLE.stringCol(roleGroup -> roleGroup.description, RoleGroup::setDescription);

    private Integer id;
    private String name;
    private String description;

    @Override
    public Integer getPrimaryKey() {
        return id;
    }

}

package com.rey.modular.user.service.model;

import com.querydsl.core.JoinType;
import com.rey.modular.common.repository.ColumnField;
import com.rey.modular.common.repository.EntityModel;
import com.rey.modular.common.repository.ModelQueryBuilder;
import com.rey.modular.common.repository.TableField;
import com.rey.modular.user.repository.entity.QRoleEntity;
import com.rey.modular.user.repository.entity.QRoleGroupEntity;
import com.rey.modular.user.repository.entity.QUserEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Optional;

@Getter
@Setter
public class User extends EntityModel<Integer, User> {

    public static final TableField<QUserEntity, QUserEntity> USER_TABLE = TableField.root(QUserEntity.userEntity);
    public static final TableField<QUserEntity, QRoleEntity> ROLE_TABLE = USER_TABLE.joinTable(JoinType.LEFTJOIN, QRoleEntity.roleEntity, (parentEntity, qRoleEntity) -> parentEntity.roleId.eq(qRoleEntity.id));
    public static final TableField<QRoleEntity, QRoleGroupEntity> ROLE_GROUP_TABLE = Role.ROLE_GROUP_TABLE.withParent(ROLE_TABLE);

    public static final ColumnField<User, QUserEntity, Integer> ID = USER_TABLE.column(qUserEntity -> qUserEntity.id, User::setId);
    public static final ColumnField<User, QUserEntity, String> NAME = USER_TABLE.column(qUserEntity -> qUserEntity.name, User::setName);
    public static final ColumnField<User, QUserEntity, String> EMAIL = USER_TABLE.column(qUserEntity -> qUserEntity.email, User::setEmail);

    public static final ColumnField<User, QRoleEntity, Integer> ROLE_TABLE_ID = Role.ID.withTable(ROLE_TABLE, User::getOrCreateRole);
    public static final ColumnField<User, QRoleEntity, String> ROLE_TABLE_NAME = Role.NAME.withTable(ROLE_TABLE, User::getOrCreateRole);
    public static final ColumnField<User, QRoleEntity, String> ROLE_TABLE_DESCRIPTION = Role.DESCRIPTION.withTable(ROLE_TABLE, User::getOrCreateRole);

    public static final ColumnField<User, QRoleGroupEntity, Integer> ROLE_GROUP_TABLE_ID = Role.ROLE_GROUP_TABLE_ID.withTable(ROLE_GROUP_TABLE, User::getOrCreateRole);
    public static final ColumnField<User, QRoleGroupEntity, String> ROLE_GROUP_TABLE_NAME = Role.ROLE_GROUP_TABLE_NAME.withTable(ROLE_GROUP_TABLE, User::getOrCreateRole);
    public static final ColumnField<User, QRoleGroupEntity, String> ROLE_GROUP_TABLE_DESCRIPTION = Role.ROLE_GROUP_TABLE_DESCRIPTION.withTable(ROLE_GROUP_TABLE, User::getOrCreateRole);

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

        public QueryBuilder(Collection<ColumnField<User, ?, ?>> columnFields) {
            super(USER_TABLE, columnFields, User::new);
        }
    }

}

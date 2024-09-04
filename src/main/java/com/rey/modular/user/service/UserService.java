package com.rey.modular.user.service;

import com.querydsl.core.types.Predicate;
import com.rey.modular.common.repository.model.Column;
import com.rey.modular.common.repository.model.ModelQuery;
import com.rey.modular.user.controller.request.UserSearchRequest;
import com.rey.modular.user.repository.RoleGroupRepository;
import com.rey.modular.user.repository.RoleRepository;
import com.rey.modular.user.repository.UserRepository;
import com.rey.modular.user.repository.entity.RoleEntity;
import com.rey.modular.user.repository.entity.RoleGroupEntity;
import com.rey.modular.user.repository.entity.UserEntity;
import com.rey.modular.user.controller.request.UserRequest;
import com.rey.modular.user.service.model.User;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleGroupRepository roleGroupRepository;

    @PostConstruct
    public void init() {
        roleGroupRepository.save(new RoleGroupEntity(1, "role_group_1", "role_group_description_1"));
        roleGroupRepository.save(new RoleGroupEntity(2, "role_group_2", "role_group_description_2"));

        roleRepository.save(new RoleEntity(1, "role_1", "role_description_1", 1));
        roleRepository.save(new RoleEntity(2, "role_2", "role_description_2", 1));
        roleRepository.save(new RoleEntity(3, "role_3", "role_description_3", 2));

        userRepository.save(new UserEntity(null, "test1", "test1", 1));
        userRepository.save(new UserEntity(null, "test2", "test2", null));
        userRepository.save(new UserEntity(null, "test3", "test3", 2));
        userRepository.save(new UserEntity(null, "test4", "test4", null));
        userRepository.save(new UserEntity(null, "test5", "test5", 3));
        userRepository.save(new UserEntity(null, "test6", "test6", null));
        userRepository.save(new UserEntity(null, "test7", "test7", 1));
        userRepository.save(new UserEntity(null, "test8", "test8", 2));
        userRepository.save(new UserEntity(null, "test9", "test9", 3));
        userRepository.save(new UserEntity(null, "test10", "test10", null));
    }

    public UserEntity createUser(UserRequest userRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userRequest.name());
        userEntity.setEmail(userRequest.email());
        userRepository.save(userEntity);
        log.info("Created user with [{}] id", userEntity.getId());
        return userEntity;
    }

    public List<UserEntity> getUserByIds(Collection<Integer> userIds) {
        log.info("Finding users by ids [{}]", userIds);
        List<UserEntity> userEntities = userRepository.findAllById(userIds);
        log.info("Found {} users", userEntities.size());
        return userEntities;
    }

    public Page<User> searchUsers(UserSearchRequest request, Collection<Column<User, ?, ?>> userFields) {
        Integer pageSize = request.getPageSizeOptional().orElse(5);
        return userRepository.findPage(new User.QueryBuilder(userFields) {
            @Override
            protected void populatePredicates(List<Predicate> predicates, ModelQuery modelQuery) {
                request.getIdOptional().ifPresent(value -> {
                    log.info("Add [{}] id to predicate", value);
                    predicates.add(User.ID.getPath(modelQuery).eq(value));
                });
                request.getRoleGroupIdOptional().ifPresent(value -> {
                    log.info("Add [{}] role group id to predicate", value);
                    predicates.add(User.ROLE_GROUP_TABLE_ID.getPath(modelQuery).eq(value));
                });
            }
        }, pageSize, request.getPageNumber(), request.getPageQueryOption());
    }
}

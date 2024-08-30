package com.rey.modular.user.service;

import com.rey.modular.user.repository.UserRepository;
import com.rey.modular.user.repository.entity.UserEntity;
import com.rey.modular.user.controller.request.UserRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}

package com.rey.modular.user.repository;

import com.rey.modular.common.repository.BaseRepository;
import com.rey.modular.user.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends BaseRepository<UserEntity, Integer> {
}

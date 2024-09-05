package com.rey.modular.user.repository;

import com.rey.modular.user.repository.entity.RoleGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleGroupRepository extends JpaRepository<RoleGroupEntity, Integer> {
}

package com.rey.modular.payment.repository;

import com.rey.modular.payment.repository.entity.BalanceMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceMovementRepository extends JpaRepository<BalanceMovementEntity, Integer> {

}

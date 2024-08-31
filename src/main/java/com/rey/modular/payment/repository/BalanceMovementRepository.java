package com.rey.modular.payment.repository;

import com.rey.modular.payment.repository.entity.BalanceMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BalanceMovementRepository extends JpaRepository<BalanceMovementEntity, Integer> {

    List<BalanceMovementEntity> findAllByOrderId(Integer orderId);

}

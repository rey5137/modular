package com.rey.modular.payment.repository;

import com.rey.modular.payment.repository.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface OrderRepository extends JpaRepository<OrderEntity, Integer>, QuerydslPredicateExecutor<OrderEntity> {
}

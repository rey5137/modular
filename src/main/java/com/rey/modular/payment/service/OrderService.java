package com.rey.modular.payment.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rey.modular.payment.repository.BalanceMovementRepository;
import com.rey.modular.payment.repository.OrderRepository;
import com.rey.modular.payment.repository.entity.BalanceMovementEntity;
import com.rey.modular.payment.repository.entity.OrderEntity;
import com.rey.modular.payment.controller.request.OrderRequest;
import com.rey.modular.payment.repository.entity.QBalanceMovementEntity;
import com.rey.modular.payment.repository.entity.QOrderEntity;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final BalanceMovementRepository balanceMovementRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public OrderService(OrderRepository orderRepository, BalanceMovementRepository balanceMovementRepository, @Qualifier("paymentModuleEntityManager") EntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.balanceMovementRepository = balanceMovementRepository;
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Transactional
    public OrderEntity createOrder(OrderRequest orderRequest) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setPayer(orderRequest.payer());
        orderEntity.setPayee(orderRequest.payee());
        orderEntity.setAmount(orderRequest.amount());
        orderEntity.setDescription(orderRequest.description());
        orderRepository.save(orderEntity);

        BalanceMovementEntity debitBalanceMovementEntity = new BalanceMovementEntity();
        debitBalanceMovementEntity.setOrderId(orderEntity.getId());
        debitBalanceMovementEntity.setAmount(orderRequest.amount());
        debitBalanceMovementEntity.setUserId(orderRequest.payer());
        debitBalanceMovementEntity.setIsDebit(true);

        BalanceMovementEntity creditBalanceMovementEntity = new BalanceMovementEntity();
        creditBalanceMovementEntity.setOrderId(orderEntity.getId());
        creditBalanceMovementEntity.setAmount(orderRequest.amount());
        creditBalanceMovementEntity.setUserId(orderRequest.payee());
        creditBalanceMovementEntity.setIsDebit(false);

        balanceMovementRepository.saveAll(List.of(debitBalanceMovementEntity, creditBalanceMovementEntity));
        log.info("Created order with [{}] id", orderEntity.getId());
        return orderEntity;
    }

    public void test(Integer id) {
        var orderEntity = QOrderEntity.orderEntity;
        var debitBalanceMovementEntity = new QBalanceMovementEntity("debitMovement");
        var creditBalanceMovementEntity = new QBalanceMovementEntity("creditMovement");
        jpaQueryFactory.select(orderEntity.id, debitBalanceMovementEntity.id, creditBalanceMovementEntity.id)
                .from(orderEntity)
                .leftJoin(debitBalanceMovementEntity)
                .on(orderEntity.id.eq(debitBalanceMovementEntity.orderId).and(debitBalanceMovementEntity.isDebit.eq(true)))
                .leftJoin(creditBalanceMovementEntity)
                .on(orderEntity.id.eq(creditBalanceMovementEntity.orderId).and(creditBalanceMovementEntity.isDebit.eq(false)))
                .where(orderEntity.id.eq(id))
                .fetchFirst();
    }

    public OrderEntity findOrderById(Integer id) {
        return jpaQueryFactory.selectFrom(QOrderEntity.orderEntity)
                .leftJoin(QBalanceMovementEntity.balanceMovementEntity)
                .on(QOrderEntity.orderEntity.id.eq(QBalanceMovementEntity.balanceMovementEntity.orderId))
                .where(QOrderEntity.orderEntity.id.eq(id))
                .fetchFirst();
    }

    public List<BalanceMovementEntity> findBalanceMovementByOrderId(Integer orderId) {
        return balanceMovementRepository.findAllByOrderId(orderId);
    }

}

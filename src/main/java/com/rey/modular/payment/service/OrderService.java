package com.rey.modular.payment.service;

import com.rey.modular.common.repository.BaseQueryService.BaseQuery;
import com.rey.modular.common.repository.BaseQueryService.SimpleQueryBuilder;
import com.rey.modular.payment.repository.BalanceMovementRepository;
import com.rey.modular.payment.repository.OrderRepository;
import com.rey.modular.payment.repository.entity.BalanceMovementEntity;
import com.rey.modular.payment.repository.entity.OrderEntity;
import com.rey.modular.payment.controller.request.OrderRequest;
import com.rey.modular.payment.repository.entity.QBalanceMovementEntity;
import com.rey.modular.payment.repository.entity.QOrderEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final BalanceMovementRepository balanceMovementRepository;
    private final PaymentQueryService paymentQueryService;

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

    public OrderEntity findOrderById(Integer id) {
        return paymentQueryService.findAll(new SimpleQueryBuilder<>(queryFactory -> new BaseQuery<>(
                queryFactory.selectFrom(QOrderEntity.orderEntity)
                        .where(QOrderEntity.orderEntity.id.eq(id)),
                OrderEntity.class
        ))).stream().findFirst().orElse(null);
    }

    public List<BalanceMovementEntity> findBalanceMovementByOrderId(Integer orderId) {
        return paymentQueryService.findAll(new SimpleQueryBuilder<>(queryFactory -> new BaseQuery<>(
                queryFactory.selectFrom(QBalanceMovementEntity.balanceMovementEntity)
                        .where(QBalanceMovementEntity.balanceMovementEntity.orderId.eq(orderId)),
                BalanceMovementEntity.class
        )));
    }

}

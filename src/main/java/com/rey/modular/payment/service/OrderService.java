package com.rey.modular.payment.service;

import com.rey.modular.payment.repository.OrderRepository;
import com.rey.modular.payment.repository.entity.OrderEntity;
import com.rey.modular.payment.request.OrderRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderEntity createOrder(OrderRequest orderRequest) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setPayer(orderRequest.payer());
        orderEntity.setPayee(orderRequest.payee());
        orderEntity.setAmount(orderRequest.amount());
        orderEntity.setDescription(orderRequest.description());
        orderRepository.save(orderEntity);
        log.info("Created order with [{}] id", orderEntity.getId());
        return orderEntity;
    }

}

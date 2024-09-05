package com.rey.modular.payment.controller;

import com.rey.modular.common.exception.ApiErrorException;
import com.rey.modular.common.response.GeneralResponse;
import com.rey.modular.payment.controller.response.BalanceMovementResponse;
import com.rey.modular.payment.controller.response.OrderResponse;
import com.rey.modular.payment.repository.entity.OrderEntity;
import com.rey.modular.payment.controller.request.OrderRequest;
import com.rey.modular.payment.controller.response.OrderIdResponse;
import com.rey.modular.payment.service.OrderService;
import com.rey.modular.user.UserApi;
import com.rey.modular.user.controller.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("${payment.module.context-path}")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserApi userApi;

    @PostMapping(value = "/orders", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GeneralResponse<OrderIdResponse>> createOrder(@RequestBody OrderRequest request) {
        log.info("--- Start to create order ---");
        try {
            userApi.getUsers(List.of(request.payer(), request.payee()), List.of(UserResponse.ID));
        }
        catch (ApiErrorException apiErrorException) {
            GeneralResponse<OrderIdResponse> generalResponses = GeneralResponse.error("not_found", "User not found");
            log.info("--- End to create order ---");
            return new ResponseEntity<>(generalResponses, HttpStatus.BAD_REQUEST);
        }
        OrderEntity orderEntity = orderService.createOrder(request);
        var orderIdResponse = new OrderIdResponse(orderEntity.getId());
        var generalResponses = GeneralResponse.success(orderIdResponse);
        log.info("--- End to create order ---");
        return new ResponseEntity<>(generalResponses, HttpStatus.OK);
    }

    @GetMapping(value = "/orders/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<GeneralResponse<OrderResponse>> getOrder(@PathVariable Integer orderId) {
        log.info("--- Start to get order ---");
        OrderEntity orderEntity = orderService.findOrderById(orderId);
        List<BalanceMovementResponse> movementResponses = orderService.findBalanceMovementByOrderId(orderId).stream()
                .map(entity -> new BalanceMovementResponse(entity.getId(), entity.getUserId(), entity.getAmount(), entity.getIsDebit()))
                .toList();
        OrderResponse orderResponse = new OrderResponse(orderEntity.getId(), orderEntity.getPayer(), orderEntity.getPayee(), orderEntity.getAmount(), orderEntity.getDescription(), movementResponses);
        var generalResponses = GeneralResponse.success(orderResponse);
        log.info("--- End to get order ---");
        return new ResponseEntity<>(generalResponses, HttpStatus.OK);
    }
}

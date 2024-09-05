package com.rey.modular.payment.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record OrderResponse(
        Integer id,
        Integer payer,
        Integer payee,
        BigDecimal amount,
        String description,
        List<BalanceMovementResponse> movements
) {
}
package com.rey.modular.payment.controller.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BalanceMovementResponse(
        Integer id,
        Integer userId,
        BigDecimal amount,
        Boolean isDebit
) {
}
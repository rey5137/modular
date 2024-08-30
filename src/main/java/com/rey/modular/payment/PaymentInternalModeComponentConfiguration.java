package com.rey.modular.payment;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(
        basePackages = "com.rey.modular.payment"
)
@ConditionalOnProperty(name = "payment.module.mode", havingValue = "internal")
public class PaymentInternalModeComponentConfiguration {

}

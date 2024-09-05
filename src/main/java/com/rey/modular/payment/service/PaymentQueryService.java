package com.rey.modular.payment.service;

import com.rey.modular.common.repository.BaseQueryService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PaymentQueryService extends BaseQueryService {

    public PaymentQueryService(@Qualifier("paymentModuleEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

}

package com.rey.modular.user.service;

import com.rey.modular.common.repository.BaseQueryService;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserQueryService extends BaseQueryService {

    public UserQueryService(@Qualifier("userModuleEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

}

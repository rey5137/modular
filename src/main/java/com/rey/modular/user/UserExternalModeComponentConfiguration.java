package com.rey.modular.user;

import com.rey.modular.user.api.UserInternalModuleApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "com.rey.modular.user.api",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = UserInternalModuleApi.class)
)
@ConditionalOnProperty(name = "user.module.mode", havingValue = "external")
public class UserExternalModeComponentConfiguration {

}

package com.rey.modular.user;

import com.rey.modular.user.api.UserExternalModuleApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(
        basePackages = "com.rey.modular.user",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = UserExternalModuleApi.class)
)
@ConditionalOnProperty(name = "user.module.mode", havingValue = "internal")
public class UserInternalModeComponentConfiguration {

}

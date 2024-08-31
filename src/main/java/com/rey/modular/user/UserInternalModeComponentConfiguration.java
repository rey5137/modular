package com.rey.modular.user;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import com.rey.modular.user.api.UserExternalModuleApi;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.discovery.HeartbeatProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulAutoRegistration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.util.UUID;

@Configuration
@ComponentScan(
        basePackages = "com.rey.modular.user",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = UserExternalModuleApi.class)
)
@ConditionalOnProperty(name = "user.module.mode", havingValue = "internal")
public class UserInternalModeComponentConfiguration {

    @Autowired
    private ConsulClient consulClient;

    @Autowired
    private HeartbeatProperties heartbeatProperties;

    @Autowired
    private ConsulDiscoveryProperties consulDiscoveryProperties;

    @Value("${server.port}")
    private Integer port;

    @Value("${user.module.name}")
    private String moduleName;

    private final String moduleUUID = UUID.randomUUID().toString();

    @PostConstruct
    public void init() {
        NewService newService = new NewService();
        newService.setId(moduleName + "_" + moduleUUID);
        newService.setName(moduleName);
        newService.setPort(port);
        newService.setCheck(ConsulAutoRegistration.createCheck(port, heartbeatProperties, consulDiscoveryProperties));
        consulClient.agentServiceRegister(newService);
    }

    @PreDestroy
    public void destroy() {
        consulClient.agentServiceDeregister(moduleName + "_" + moduleUUID);
    }

}

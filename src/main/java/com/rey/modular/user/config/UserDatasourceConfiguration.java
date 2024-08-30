package com.rey.modular.user.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "userModuleEntityManager",
        transactionManagerRef = "userModuleTransactionManager",
        basePackages = "com.rey.modular.user.repository"
)
public class UserDatasourceConfiguration {

    @Value("${user.module.spring.datasource.username}")
    String username;

    @Value("${user.module.spring.datasource.password}")
    String password;

    @Value("${user.module.spring.datasource.url}")
    String url;

    @Bean
    @ConfigurationProperties(prefix = "user.module.spring.datasource.hikari")
    public DataSource userModuleDataSource() {
        return DataSourceBuilder
                .create()
                .username(username)
                .password(password)
                .url(url)
                .build();
    }

    @Bean(name = "userModuleEntityManager")
    public LocalContainerEntityManagerFactoryBean userModuleEntityManager(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(userModuleDataSource())
                .packages("com.rey.modular.user.repository.entity")
                .persistenceUnit("userModulePU")
                .build();
    }

    @Bean(name = "userModuleTransactionManager")
    public PlatformTransactionManager userModuleTransactionManager(@Qualifier("userModuleEntityManager") EntityManagerFactory userModuleEntityManager) {
        return new JpaTransactionManager(userModuleEntityManager);
    }

}

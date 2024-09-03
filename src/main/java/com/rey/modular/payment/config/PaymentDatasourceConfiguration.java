package com.rey.modular.payment.config;

import com.rey.modular.common.repository.BaseRepositoryImpl;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "paymentModuleEntityManager",
        transactionManagerRef = "paymentModuleTransactionManager",
        basePackages = "com.rey.modular.payment.repository",
        repositoryBaseClass = BaseRepositoryImpl.class
)
public class PaymentDatasourceConfiguration {

    @Value("${payment.module.spring.datasource.username}")
    String username;

    @Value("${payment.module.spring.datasource.password}")
    String password;

    @Value("${payment.module.spring.datasource.url}")
    String url;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "payment.module.spring.datasource.hikari")
    public DataSource paymentModuleDataSource() {
        return DataSourceBuilder
                .create()
                .username(username)
                .password(password)
                .url(url)
                .build();
    }

    @Bean(name = "paymentModuleEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean paymentModuleEntityManager(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(paymentModuleDataSource())
                .packages("com.rey.modular.payment.repository.entity")
                .persistenceUnit("paymentModulePU")
                .build();
    }

    @Bean(name = "paymentModuleTransactionManager")
    @Primary
    public PlatformTransactionManager paymentModuleTransactionManager(@Qualifier("paymentModuleEntityManager") EntityManagerFactory paymentModuleEntityManager) {
        return new JpaTransactionManager(paymentModuleEntityManager);
    }

}

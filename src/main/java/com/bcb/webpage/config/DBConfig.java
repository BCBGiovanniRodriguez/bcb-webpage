package com.bcb.webpage.config;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "backendEntityManagerFactory",
    transactionManagerRef = "backendTransactionManager",
    basePackages = {
        "com.bcb.webpage.model.backend.*"
    }
)
public class DBConfig {

    @Primary
    @Bean(name = "backendDatasource")
    @ConfigurationProperties("backend.datasource")
    public DataSource backendDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "backendEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean backendEntityManagerFactory(EntityManagerFactoryBuilder builder,
        @Qualifier("backendDatasource") DataSource backendDataSource) {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        //properties.put("hibernate.connection.username", "sisbur");
        //properties.put("hibernate.connection.password", "Bur5ametric@");
        //properties.put("jakarta.persistence.jdbc.url", "jdbc:oracle:thin:@10.20.50.200:1525:BMTKSIS");
        //properties.put("jakarta.persistence.jdbc.driver", "oracle.jdbc.OracleDriver");

        return builder.dataSource(backendDataSource)
            //.properties(properties)
            .packages("com.bcb.webpage.model.backend.*")
            .persistenceUnit("backend")
            .build();
    }

    @Primary
    @Bean(name = "backendTransactionManager")
    public PlatformTransactionManager transactionManager(
        @Qualifier("backendEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean(name = "backendNamedParameterJdbcTemplate")
    @DependsOn("backendDatasource")
    public NamedParameterJdbcTemplate sisburNamedParameterJdbcTemplate(@Qualifier("backendDatasource") DataSource backendDataSource) {
        return new NamedParameterJdbcTemplate(backendDataSource);
    }
}

package com.bcb.webpage.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;



@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "sisfiscalEntityManagerFactory",
    transactionManagerRef = "sisfiscalTransactionManager",
    basePackages = {
        "com.bcb.webpage.model.sisfiscal.*"
    }
)
public class SisFiscalDbConfig {

    @Bean(name = "sisfiscalDataSource")
    @ConfigurationProperties("spring.datasource.sisfiscal")
    public DataSource sisfiscalDataSource() {
        return (DataSource) DataSourceBuilder.create().build();
    }

    @Bean(name = "sisfiscalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sisfiscalEntityManagerFactory(
        EntityManagerFactoryBuilder builder, @Qualifier("sisfiscalDataSource") DataSource sisfiscalDataSource
    ) {

        return builder.dataSource(sisfiscalDataSource)
            .packages("com.bcb.webpage.model.sisfiscal.*")
            .persistenceUnit("sisfiscal")
            .build();
    }

    @Bean(name = "sisfiscalTransactionManager")
    public PlatformTransactionManager transactionManager(@NonNull
        @Qualifier("sisfiscalEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
    }
    
    @Bean(name = "sisfiscalJdbcTemplate")
    @DependsOn("sisfiscalDataSource")
    public JdbcTemplate sisfiscalJdbcTemplate(@NonNull
        @Qualifier("sisfiscalDataSource") DataSource sisfiscalDataSource) {
        return new JdbcTemplate(sisfiscalDataSource);
    }

}

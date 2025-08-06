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
    entityManagerFactoryRef = "webpageEntityManagerFactory",
    transactionManagerRef = "webpageTransactionManager",
    basePackages = {
        "com.bcb.webpage.model.webpage.*"
    }
)
public class DBConfig {

    @Primary
    @Bean(name = "webpageDatasource")
    @ConfigurationProperties("webpage.datasource")
    public DataSource webpageDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "webpageEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean webpageEntityManagerFactory(EntityManagerFactoryBuilder builder,
        @Qualifier("webpageDatasource") DataSource webpageDataSource) {

        return builder.dataSource(webpageDataSource)
            .packages("com.bcb.webpage.model.webpage.*")
            .persistenceUnit("webpage")
            .build();
    }

    @Primary
    @Bean(name = "webpageTransactionManager")
    public PlatformTransactionManager transactionManager(
        @Qualifier("webpageEntityManagerFactory") EntityManagerFactory webpageEntityManagerFactory) {
            return new JpaTransactionManager(webpageEntityManagerFactory);
    }

    @Primary
    @Bean(name = "webpageNamedParameterJdbcTemplate")
    @DependsOn("webpageDatasource")
    public NamedParameterJdbcTemplate webpageNamedParameterJdbcTemplate(@Qualifier("webpageDatasource") DataSource webpageDataSource) {
        return new NamedParameterJdbcTemplate(webpageDataSource);
    }
}

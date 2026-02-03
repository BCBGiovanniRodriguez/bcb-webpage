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
    entityManagerFactoryRef = "sisburEntityManagerFactory",
    transactionManagerRef = "sisburTransactionManager",
    basePackages = {
        "com.bcb.webpage.model.sisbur.*"
    }
)
public class SisburDBConfig {

    @Bean(name = "sisburDatasource")
    @ConfigurationProperties("spring.datasource.sisbur")
    public DataSource sisburDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sisburEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sisburEntityManagerFactory(EntityManagerFactoryBuilder builder,
        @Qualifier("sisburDatasource") DataSource sisburDataSource) {

        return builder.dataSource(sisburDataSource)
            .packages("com.bcb.webpage.model.sisbur.*")
            .persistenceUnit("sisbur")
            .build();
    }

    @Bean(name = "sisburTransactionManager")
    public PlatformTransactionManager transactionManager(@NonNull
        @Qualifier("sisburEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
    }
    
    @Bean(name = "sisburJdbcTemplate")
    @DependsOn("sisburDatasource")
    public JdbcTemplate sisburJdbcTemplate(@NonNull
        @Qualifier("sisburDatasource") DataSource sisburDataSource) {
        return new JdbcTemplate(sisburDataSource);
    }

    /*
    @Bean(name = "sisburNamedParameterJdbcTemplate")
    @DependsOn("sisburDatasource")
    public NamedParameterJdbcTemplate sisburNamedParameterJdbcTemplate(@Qualifier("sisburDatasource") DataSource sisburDataSource) {
        return new NamedParameterJdbcTemplate(sisburDataSource);
    }
    */
}

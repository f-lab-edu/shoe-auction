package com.flab.shoeauction.common.config;

import com.flab.shoeauction.common.db.DynamicRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@RequiredArgsConstructor
@Configuration
@EnableTransactionManagement
public class RoutingDataSourceConfig {

    private final Environment env;

    @ConfigurationProperties(prefix = "spring.datasource.hikari.master")
    @Bean
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @ConfigurationProperties(prefix = "spring.datasource.hikari.slave")
    @Bean
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @DependsOn({"masterDataSource", "slaveDataSource"})
    @Bean
    public DataSource routingDataSource(
        @Qualifier("masterDataSource") DataSource master,
        @Qualifier("slaveDataSource") DataSource slave) {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();

        dataSourceMap.put("master", master);
        dataSourceMap.put("slave", slave);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(master);

        return routingDataSource;
    }

    @DependsOn({"routingDataSource"})
    @Bean
    public DataSource dataSource(DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.flab.shoeauction.domain");
        em.setPersistenceUnitName("master");

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy",
            SpringPhysicalNamingStrategy.class.getName());
        properties.put("hibernate.implicit_naming_strategy",
            SpringImplicitNamingStrategy.class.getName());
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(
        EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }
}

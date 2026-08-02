package com.tallerwebi.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class DatabaseInitializationConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    @DependsOn("sessionFactory")
    public DataSourceInitializer dataSourceInitializer() {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);

        boolean usuariosVacios = isTablaUsuariosVacia();

        if (usuariosVacios) {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("data.sql"));
            initializer.setDatabasePopulator(populator);
        } else {
            initializer.setDatabasePopulator(new ResourceDatabasePopulator());
        }

        return initializer;
    }

    private boolean isTablaUsuariosVacia() {
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM USUARIO", Integer.class);
            return count == null || count == 0;
        } catch (Exception e) {
            return true;
        }
    }
}
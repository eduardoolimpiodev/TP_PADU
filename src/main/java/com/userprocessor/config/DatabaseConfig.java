package com.userprocessor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        logger.info("=== CONFIGURING DATASOURCE PROPERTIES ===");
        
        String databaseUrl = System.getenv("DATABASE_URL");
        logger.info("Environment DATABASE_URL: {}", databaseUrl);
        
        DataSourceProperties properties = new DataSourceProperties();
        
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                logger.info("Parsing Railway DATABASE_URL...");
                URI dbUri = new URI(databaseUrl);
                
                String username = null;
                String password = null;
                
                if (dbUri.getUserInfo() != null) {
                    String[] userInfo = dbUri.getUserInfo().split(":");
                    username = userInfo[0];
                    if (userInfo.length > 1) {
                        password = userInfo[1];
                    }
                }
                
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s",
                        dbUri.getHost(),
                        dbUri.getPort(),
                        dbUri.getPath());
                
                logger.info("Parsed JDBC URL: {}", jdbcUrl);
                logger.info("Username: {}", username);
                logger.info("Host: {}", dbUri.getHost());
                logger.info("Port: {}", dbUri.getPort());
                logger.info("Path: {}", dbUri.getPath());
                
                properties.setUrl(jdbcUrl);
                properties.setUsername(username);
                properties.setPassword(password);
                properties.setDriverClassName("org.postgresql.Driver");
                
                logger.info("DataSource properties configured successfully!");
                
            } catch (Exception e) {
                logger.error("Failed to parse DATABASE_URL: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to configure database", e);
            }
        } else {
            logger.info("=== LOCAL H2 DATABASE CONFIG ===");
            logger.info("DATABASE_URL not found, using H2 for local development");
            
            properties.setUrl("jdbc:h2:mem:testdb");
            properties.setUsername("sa");
            properties.setPassword("");
            properties.setDriverClassName("org.h2.Driver");
        }
        
        return properties;
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        logger.info("=== CREATING DATASOURCE BEAN ===");
        logger.info("Final JDBC URL: {}", properties.getUrl());
        logger.info("Final Username: {}", properties.getUsername());
        
        return properties.initializeDataSourceBuilder().build();
    }
}

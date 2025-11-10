package com.userprocessor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "DATABASE_URL")
    public DataSource railwayDataSource() {
        logger.info("=== RAILWAY DATABASE CONFIG ===");
        logger.info("Creating DataSource with DATABASE_URL: {}", databaseUrl);
        
        try {
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
            
            DataSource ds = DataSourceBuilder.create()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .driverClassName("org.postgresql.Driver")
                    .build();
                    
            logger.info("DataSource created successfully!");
            return ds;
                    
        } catch (Exception e) {
            logger.error("Failed to parse DATABASE_URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to configure database", e);
        }
    }
    
    @Bean
    @ConditionalOnProperty(name = "DATABASE_URL", havingValue = "", matchIfMissing = true)
    public DataSource localDataSource() {
        logger.info("=== LOCAL H2 DATABASE CONFIG ===");
        logger.info("DATABASE_URL not found, using H2 for local development");
        
        return DataSourceBuilder.create()
                .url("jdbc:h2:mem:testdb")
                .username("sa")
                .password("")
                .driverClassName("org.h2.Driver")
                .build();
    }
}

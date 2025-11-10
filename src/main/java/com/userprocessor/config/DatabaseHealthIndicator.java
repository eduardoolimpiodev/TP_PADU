package com.userprocessor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuator.health.Health;
import org.springframework.boot.actuator.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            String driver = connection.getMetaData().getDriverName();
            
            return Health.up()
                    .withDetail("database", driver)
                    .withDetail("url", maskPassword(url))
                    .withDetail("status", "Connected")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "Connection failed")
                    .build();
        }
    }
    
    private String maskPassword(String url) {
        return url.replaceAll("://[^:]+:[^@]+@", "://***:***@");
    }
}

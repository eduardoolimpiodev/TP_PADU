package com.userprocessor.controller;

import com.userprocessor.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "Operations for retrieving system statistics and metrics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getGeneralStatistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> stats = statisticsService.getGeneralStatistics();
            
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving statistics");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/sources")
    public ResponseEntity<Map<String, Object>> getSourceStatistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Long> sourceStats = statisticsService.getSourceStatistics();
            
            response.put("success", true);
            response.put("data", sourceStats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving source statistics");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/sources/{source}")
    public ResponseEntity<Map<String, Object>> getCountBySource(@PathVariable String source) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            long count = statisticsService.getUserCountBySource(source);
            
            response.put("success", true);
            response.put("data", Map.of(
                "source", source,
                "count", count
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving count for source: " + source);
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> getDetailedStatistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> detailedStats = statisticsService.getDetailedStatistics();
            
            response.put("success", true);
            response.put("data", detailedStats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving detailed statistics");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

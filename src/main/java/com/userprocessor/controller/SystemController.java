package com.userprocessor.controller;

import com.userprocessor.service.FileProcessingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "System", description = "System health and information endpoints")
public class SystemController {

    private final FileProcessingService fileProcessingService;

    @Autowired
    public SystemController(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "UP");
        response.put("message", "User Data Processor API is running");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("application", "User Data Processor");
        response.put("version", "1.0.0");
        response.put("description", "System for processing and storing user data from CSV, JSON, and XML files");
        response.put("supportedFileTypes", fileProcessingService.getSupportedFileTypes());
        response.put("supportedOutputFormats", new String[]{"json", "csv", "xml"});
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/supported-types")
    public ResponseEntity<Map<String, Object>> getSupportedTypes() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("success", true);
        response.put("data", Map.of(
            "inputFormats", fileProcessingService.getSupportedFileTypes(),
            "outputFormats", new String[]{"json", "csv", "xml"}
        ));
        
        return ResponseEntity.ok(response);
    }
}

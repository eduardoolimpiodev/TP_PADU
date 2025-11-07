package com.userprocessor.controller;

import com.userprocessor.dto.ProcessingResult;
import com.userprocessor.dto.UserResponseDto;
import com.userprocessor.enums.OutputFormat;
import com.userprocessor.service.FileProcessingService;
import com.userprocessor.service.OutputFormatterService;
import com.userprocessor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FileProcessingService fileProcessingService;
    private final OutputFormatterService outputFormatterService;

    @Autowired
    public UserController(
            UserService userService,
            FileProcessingService fileProcessingService,
            OutputFormatterService outputFormatterService) {
        this.userService = userService;
        this.fileProcessingService = fileProcessingService;
        this.outputFormatterService = outputFormatterService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") String fileType) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            ProcessingResult result = fileProcessingService.processFile(file, fileType);
            
            response.put("success", true);
            response.put("message", "File processed successfully");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error processing file");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "json") String format,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            OutputFormat outputFormat = OutputFormat.fromString(format);
            
            if (outputFormat == OutputFormat.JSON) {
                Page<UserResponseDto> users = userService.getAllUsers(page, size);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", users.getContent());
                response.put("pagination", Map.of(
                    "page", users.getNumber(),
                    "size", users.getSize(),
                    "totalElements", users.getTotalElements(),
                    "totalPages", users.getTotalPages()
                ));
                
                return ResponseEntity.ok(response);
            } else {
                List<UserResponseDto> users = userService.getAllUsers();
                String formattedData = outputFormatterService.formatUsers(users, outputFormat);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(outputFormat.getContentType()));
                headers.setContentDispositionFormData("attachment", 
                    "users" + outputFormatterService.getFileExtension(outputFormat));
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(formattedData);
            }
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error retrieving users");
            errorResponse.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<UserResponseDto> user = userService.getUserById(id);
        
        if (user.isPresent()) {
            response.put("success", true);
            response.put("data", user.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable String email) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<UserResponseDto> user = userService.getUserByEmail(email);
        
        if (user.isPresent()) {
            response.put("success", true);
            response.put("data", user.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/source/{source}")
    public ResponseEntity<Map<String, Object>> getUsersBySource(
            @PathVariable String source,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Page<UserResponseDto> users = userService.getUsersBySource(source, page, size);
            
            response.put("success", true);
            response.put("data", users.getContent());
            response.put("pagination", Map.of(
                "page", users.getNumber(),
                "size", users.getSize(),
                "totalElements", users.getTotalElements(),
                "totalPages", users.getTotalPages()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving users by source");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<UserResponseDto> user = userService.getUserById(id);
            
            if (user.isPresent()) {
                userService.deleteUser(id);
                response.put("success", true);
                response.put("message", "User deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error deleting user");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

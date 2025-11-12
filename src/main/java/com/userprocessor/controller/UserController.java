package com.userprocessor.controller;

import com.userprocessor.dto.ProcessingResult;
import com.userprocessor.dto.UserDto;
import com.userprocessor.dto.UserResponseDto;
import com.userprocessor.entity.User;
import com.userprocessor.enums.OutputFormat;
import com.userprocessor.service.FileProcessingService;
import com.userprocessor.service.OutputFormatterService;
import com.userprocessor.service.UserService;
import com.userprocessor.validation.ValidFileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "Operations for managing users and file processing")
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

    @Operation(
        summary = "Upload and process user data file",
        description = "Upload a CSV, JSON, or XML file containing user data for processing and storage"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "File processed successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProcessingResult.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "message": "File processed successfully",
                      "data": {
                        "totalRecords": 100,
                        "processedRecords": 95,
                        "skippedRecords": 3,
                        "errorRecords": 2
                      }
                    }
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid file or validation error"),
        @ApiResponse(responseCode = "413", description = "File size exceeds limit"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @Parameter(description = "File to upload (CSV, JSON, or XML)", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "File type", required = true, example = "csv")
            @RequestParam("fileType") @ValidFileType String fileType) {
        
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

    @Operation(
        summary = "Create a new user",
        description = "Create a single user with name and email"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User with email already exists")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(
            @Parameter(description = "User data", required = true)
            @RequestBody UserDto userDto) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user = new User();
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setSource("manual");
            
            User savedUser = userService.saveUser(user);
            
            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("data", savedUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating user");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(
        summary = "Get all users with optional formatting",
        description = "Retrieve all users with pagination and format options (JSON, CSV, XML)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @Parameter(description = "Output format", example = "json")
            @RequestParam(defaultValue = "json") String format,
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
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

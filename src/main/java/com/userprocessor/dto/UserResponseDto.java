package com.userprocessor.dto;

import com.userprocessor.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "User response data transfer object")
public class UserResponseDto {

    @Schema(description = "User unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "User full name", example = "John Doe")
    private String name;
    
    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "File source type", example = "csv")
    private String source;
    
    @Schema(description = "User creation timestamp")
    private LocalDateTime createdAt;
    
    @Schema(description = "User last update timestamp")
    private LocalDateTime updatedAt;

    public UserResponseDto() {}

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.source = user.getSource();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "UserResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", source='" + source + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

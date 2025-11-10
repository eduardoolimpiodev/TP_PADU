package com.userprocessor.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response with JWT token")
public class AuthResponseDto {
    
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "Token type", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "Token expiration time in seconds", example = "3600")
    private Long expiresIn;
    
    @Schema(description = "User information")
    private UserInfoDto user;
    
    public AuthResponseDto() {}
    
    public AuthResponseDto(String accessToken, Long expiresIn, UserInfoDto user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public UserInfoDto getUser() {
        return user;
    }
    
    public void setUser(UserInfoDto user) {
        this.user = user;
    }
    
    @Schema(description = "User information")
    public static class UserInfoDto {
        @Schema(description = "User ID", example = "1")
        private Long id;
        
        @Schema(description = "Username", example = "john_doe")
        private String username;
        
        @Schema(description = "Email", example = "john.doe@example.com")
        private String email;
        
        @Schema(description = "User role", example = "USER")
        private String role;
        
        public UserInfoDto() {}
        
        public UserInfoDto(Long id, String username, String email, String role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
}

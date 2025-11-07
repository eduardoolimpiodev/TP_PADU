package com.userprocessor.processor.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.userprocessor.dto.UserDto;
import com.userprocessor.enums.FileType;
import com.userprocessor.exception.FileProcessingException;
import com.userprocessor.processor.BaseFileProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JsonFileProcessor extends BaseFileProcessor {

    private final ObjectMapper objectMapper;

    public JsonFileProcessor() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public FileType getSupportedFileType() {
        return FileType.JSON;
    }

    @Override
    public List<UserDto> processFile(MultipartFile file) throws Exception {
        validateFileFormat(file);

        List<UserDto> users = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(file.getInputStream());

            if (!rootNode.isArray()) {
                throw new FileProcessingException("JSON file must contain an array of user objects");
            }

            if (rootNode.size() == 0) {
                throw new FileProcessingException("JSON array is empty");
            }

            for (int i = 0; i < rootNode.size(); i++) {
                JsonNode userNode = rootNode.get(i);
                
                if (!userNode.isObject()) {
                    throw new FileProcessingException("Invalid user object at index " + i);
                }

                String name = getStringValue(userNode, "name");
                String email = getStringValue(userNode, "email");

                if ((name == null || name.trim().isEmpty()) && 
                    (email == null || email.trim().isEmpty())) {
                    continue;
                }

                UserDto userDto = new UserDto(
                    name != null ? name.trim() : "",
                    email != null ? email.trim() : ""
                );

                validateUserData(userDto, i + 1);
                users.add(userDto);
            }

        } catch (IOException e) {
            throw new FileProcessingException("Error reading JSON file: " + e.getMessage(), e);
        } catch (Exception e) {
            if (e instanceof FileProcessingException) {
                throw e;
            }
            throw new FileProcessingException("Error processing JSON file: " + e.getMessage(), e);
        }

        return users;
    }

    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        return fieldNode.asText();
    }

    @Override
    public void validateFileFormat(MultipartFile file) throws Exception {
        super.validateFileFormat(file);

        try {
            JsonNode rootNode = objectMapper.readTree(file.getInputStream());
            if (!rootNode.isArray()) {
                throw new FileProcessingException("JSON file must contain an array of objects");
            }
        } catch (IOException e) {
            throw new FileProcessingException("Invalid JSON format: " + e.getMessage(), e);
        }
    }
}

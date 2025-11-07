package com.userprocessor.service;

import com.userprocessor.config.ProcessingConfig;
import com.userprocessor.enums.FileType;
import com.userprocessor.exception.FileProcessingException;
import com.userprocessor.util.FileValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class FileValidationService {

    private final ProcessingConfig processingConfig;

    @Autowired
    public FileValidationService(ProcessingConfig processingConfig) {
        this.processingConfig = processingConfig;
    }

    public ValidationResult validateFile(MultipartFile file, String expectedFileType) {
        ValidationResult result = new ValidationResult();
        
        if (file == null || file.isEmpty()) {
            result.addError("File is empty or null");
            return result;
        }

        validateFileSize(file, result);
        validateFileName(file, result);
        validateFileType(file, expectedFileType, result);
        validateMimeType(file, expectedFileType, result);

        result.setValid(result.getErrors().isEmpty());
        return result;
    }

    private void validateFileSize(MultipartFile file, ValidationResult result) {
        if (file.getSize() > processingConfig.getMaxFileSize()) {
            result.addError(String.format("File size (%s) exceeds maximum allowed size (%s)",
                FileValidationUtil.formatFileSize(file.getSize()),
                FileValidationUtil.formatFileSize(processingConfig.getMaxFileSize())));
        }
    }

    private void validateFileName(MultipartFile file, ValidationResult result) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            result.addError("File name is required");
            return;
        }

        if (filename.length() > 255) {
            result.addError("File name is too long (maximum 255 characters)");
        }

        String extension = FileValidationUtil.getFileExtension(filename);
        if (extension.isEmpty()) {
            result.addError("File must have an extension");
        }
    }

    private void validateFileType(MultipartFile file, String expectedFileType, ValidationResult result) {
        try {
            FileType expectedType = FileType.fromString(expectedFileType);
            FileType detectedType = FileValidationUtil.detectFileTypeFromName(file.getOriginalFilename());
            
            if (detectedType == null) {
                result.addError("Could not detect file type from filename");
                return;
            }

            if (!expectedType.equals(detectedType)) {
                result.addError(String.format("File type mismatch. Expected: %s, Detected: %s",
                    expectedType.getValue(), detectedType.getValue()));
            }
        } catch (IllegalArgumentException e) {
            result.addError("Invalid file type: " + expectedFileType);
        }
    }

    private void validateMimeType(MultipartFile file, String expectedFileType, ValidationResult result) {
        try {
            FileType fileType = FileType.fromString(expectedFileType);
            if (!FileValidationUtil.isValidMimeType(file, fileType)) {
                result.addWarning(String.format("MIME type '%s' may not be appropriate for %s files",
                    file.getContentType(), fileType.getValue().toUpperCase()));
            }
        } catch (IllegalArgumentException e) {
        }
    }

    public static class ValidationResult {
        private boolean valid = true;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void addError(String error) {
            this.errors.add(error);
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public void addWarning(String warning) {
            this.warnings.add(warning);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
    }
}

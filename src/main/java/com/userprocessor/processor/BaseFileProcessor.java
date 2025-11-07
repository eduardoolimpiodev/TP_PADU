package com.userprocessor.processor;

import com.userprocessor.dto.UserDto;
import com.userprocessor.enums.FileType;
import com.userprocessor.exception.FileProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public abstract class BaseFileProcessor implements FileProcessor {

    @Override
    public boolean canProcess(FileType fileType) {
        return getSupportedFileType().equals(fileType);
    }

    @Override
    public void validateFileFormat(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new FileProcessingException("File is empty or null");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new FileProcessingException("File size exceeds 10MB limit");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileProcessingException("File name is required");
        }

        String fileExtension = getFileExtension(originalFilename);
        if (!getSupportedFileType().getValue().equalsIgnoreCase(fileExtension)) {
            throw new FileProcessingException(
                String.format("Invalid file extension. Expected: %s, Found: %s", 
                    getSupportedFileType().getValue(), fileExtension)
            );
        }
    }

    protected String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    protected void validateUserData(UserDto userDto, int lineNumber) throws FileProcessingException {
        if (userDto == null) {
            throw new FileProcessingException("User data is null at line " + lineNumber);
        }

        if (userDto.getName() == null || userDto.getName().trim().isEmpty()) {
            throw new FileProcessingException("Name is required at line " + lineNumber);
        }

        if (userDto.getEmail() == null || userDto.getEmail().trim().isEmpty()) {
            throw new FileProcessingException("Email is required at line " + lineNumber);
        }

        if (!isValidEmail(userDto.getEmail())) {
            throw new FileProcessingException("Invalid email format at line " + lineNumber + ": " + userDto.getEmail());
        }

        if (userDto.getName().length() > 255) {
            throw new FileProcessingException("Name exceeds 255 characters at line " + lineNumber);
        }

        if (userDto.getEmail().length() > 255) {
            throw new FileProcessingException("Email exceeds 255 characters at line " + lineNumber);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && 
               email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    @Override
    public abstract List<UserDto> processFile(MultipartFile file) throws Exception;
}

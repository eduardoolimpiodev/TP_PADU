package com.userprocessor.dto;

import com.userprocessor.validation.ValidFileType;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadRequest {

    @NotNull(message = "File is required")
    private MultipartFile file;

    @NotNull(message = "File type is required")
    @ValidFileType
    private String fileType;

    public FileUploadRequest() {}

    public FileUploadRequest(MultipartFile file, String fileType) {
        this.file = file;
        this.fileType = fileType;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}

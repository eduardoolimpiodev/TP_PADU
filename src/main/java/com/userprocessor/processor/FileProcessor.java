package com.userprocessor.processor;

import com.userprocessor.dto.UserDto;
import com.userprocessor.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileProcessor {

    FileType getSupportedFileType();

    List<UserDto> processFile(MultipartFile file) throws Exception;

    boolean canProcess(FileType fileType);

    void validateFileFormat(MultipartFile file) throws Exception;
}

package com.userprocessor.service;

import com.userprocessor.dto.ProcessingResult;
import com.userprocessor.enums.FileType;
import com.userprocessor.exception.FileProcessingException;
import com.userprocessor.exception.UnsupportedFileTypeException;
import com.userprocessor.factory.FileProcessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileProcessingService {

    private final UserService userService;
    private final FileValidationService fileValidationService;
    private final FileProcessorFactory fileProcessorFactory;

    @Autowired
    public FileProcessingService(
            UserService userService,
            FileValidationService fileValidationService,
            FileProcessorFactory fileProcessorFactory) {
        this.userService = userService;
        this.fileValidationService = fileValidationService;
        this.fileProcessorFactory = fileProcessorFactory;
    }

    public ProcessingResult processFile(MultipartFile file, String fileType) throws Exception {
        if (!FileType.isValid(fileType)) {
            throw new UnsupportedFileTypeException("Unsupported file type: " + fileType);
        }

        FileValidationService.ValidationResult validationResult = 
            fileValidationService.validateFile(file, fileType);

        if (!validationResult.isValid()) {
            ProcessingResult result = new ProcessingResult();
            result.setTotalRecords(0);
            result.setProcessedRecords(0);
            result.setSkippedRecords(0);
            result.setErrorRecords(1);
            
            for (String error : validationResult.getErrors()) {
                result.addError(error);
            }
            
            for (String warning : validationResult.getWarnings()) {
                result.addWarning(warning);
            }
            
            return result;
        }

        try {
            ProcessingResult result = userService.processFileUpload(file, fileType);
            
            for (String warning : validationResult.getWarnings()) {
                result.addWarning(warning);
            }
            
            return result;
            
        } catch (Exception e) {
            throw new FileProcessingException("Error processing file: " + e.getMessage(), e);
        }
    }

    public boolean isFileTypeSupported(String fileType) {
        return fileProcessorFactory.isSupported(fileType);
    }

    public String[] getSupportedFileTypes() {
        return new String[]{"csv", "json", "xml"};
    }
}

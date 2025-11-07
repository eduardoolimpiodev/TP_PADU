package com.userprocessor.factory;

import com.userprocessor.enums.FileType;
import com.userprocessor.exception.UnsupportedFileTypeException;
import com.userprocessor.processor.FileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FileProcessorFactory {

    private final Map<FileType, FileProcessor> processors;

    @Autowired
    public FileProcessorFactory(List<FileProcessor> fileProcessors) {
        this.processors = fileProcessors.stream()
                .collect(Collectors.toMap(
                    FileProcessor::getSupportedFileType,
                    Function.identity()
                ));
    }

    public FileProcessor getProcessor(FileType fileType) {
        FileProcessor processor = processors.get(fileType);
        if (processor == null) {
            throw new UnsupportedFileTypeException("No processor found for file type: " + fileType);
        }
        return processor;
    }

    public FileProcessor getProcessor(String fileTypeString) {
        try {
            FileType fileType = FileType.fromString(fileTypeString);
            return getProcessor(fileType);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedFileTypeException("Unsupported file type: " + fileTypeString);
        }
    }

    public boolean isSupported(FileType fileType) {
        return processors.containsKey(fileType);
    }

    public boolean isSupported(String fileTypeString) {
        try {
            FileType fileType = FileType.fromString(fileTypeString);
            return isSupported(fileType);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

package com.userprocessor.processor.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.userprocessor.dto.UserDto;
import com.userprocessor.enums.FileType;
import com.userprocessor.exception.FileProcessingException;
import com.userprocessor.processor.BaseFileProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvFileProcessor extends BaseFileProcessor {

    @Override
    public FileType getSupportedFileType() {
        return FileType.CSV;
    }

    @Override
    public List<UserDto> processFile(MultipartFile file) throws Exception {
        validateFileFormat(file);

        List<UserDto> users = new ArrayList<>();
        
        try (CSVReader csvReader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            List<String[]> records = csvReader.readAll();
            
            if (records.isEmpty()) {
                throw new FileProcessingException("CSV file is empty");
            }

            String[] headers = records.get(0);
            validateHeaders(headers);

            int nameIndex = findHeaderIndex(headers, "name");
            int emailIndex = findHeaderIndex(headers, "email");

            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                
                if (record.length < Math.max(nameIndex, emailIndex) + 1) {
                    throw new FileProcessingException("Insufficient columns at line " + (i + 1));
                }

                String name = record[nameIndex] != null ? record[nameIndex].trim() : "";
                String email = record[emailIndex] != null ? record[emailIndex].trim() : "";

                if (name.isEmpty() && email.isEmpty()) {
                    continue;
                }

                UserDto userDto = new UserDto(name, email);
                validateUserData(userDto, i + 1);
                users.add(userDto);
            }

        } catch (IOException e) {
            throw new FileProcessingException("Error reading CSV file", e);
        } catch (CsvException e) {
            throw new FileProcessingException("Error parsing CSV file: " + e.getMessage(), e);
        }

        return users;
    }

    private void validateHeaders(String[] headers) throws FileProcessingException {
        if (headers.length < 2) {
            throw new FileProcessingException("CSV must have at least 2 columns: name and email");
        }

        boolean hasName = false;
        boolean hasEmail = false;

        for (String header : headers) {
            if (header != null) {
                String headerLower = header.trim().toLowerCase();
                if ("name".equals(headerLower)) {
                    hasName = true;
                } else if ("email".equals(headerLower)) {
                    hasEmail = true;
                }
            }
        }

        if (!hasName) {
            throw new FileProcessingException("CSV must have a 'name' column");
        }
        if (!hasEmail) {
            throw new FileProcessingException("CSV must have an 'email' column");
        }
    }

    private int findHeaderIndex(String[] headers, String headerName) throws FileProcessingException {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i] != null && headerName.equalsIgnoreCase(headers[i].trim())) {
                return i;
            }
        }
        throw new FileProcessingException("Header '" + headerName + "' not found in CSV");
    }
}

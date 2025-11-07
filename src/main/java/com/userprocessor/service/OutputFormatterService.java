package com.userprocessor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.CSVWriter;
import com.userprocessor.dto.UserResponseDto;
import com.userprocessor.enums.OutputFormat;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Service
public class OutputFormatterService {

    private final ObjectMapper objectMapper;
    private final XmlMapper xmlMapper;

    public OutputFormatterService() {
        this.objectMapper = new ObjectMapper();
        this.xmlMapper = new XmlMapper();
    }

    public String formatUsers(List<UserResponseDto> users, OutputFormat format) throws Exception {
        switch (format) {
            case JSON:
                return formatAsJson(users);
            case CSV:
                return formatAsCsv(users);
            case XML:
                return formatAsXml(users);
            default:
                throw new IllegalArgumentException("Unsupported output format: " + format);
        }
    }

    private String formatAsJson(List<UserResponseDto> users) throws Exception {
        return objectMapper.writeValueAsString(users);
    }

    private String formatAsCsv(List<UserResponseDto> users) throws Exception {
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        String[] headers = {"id", "name", "email", "source", "createdAt", "updatedAt"};
        csvWriter.writeNext(headers);

        for (UserResponseDto user : users) {
            String[] row = {
                user.getId() != null ? user.getId().toString() : "",
                user.getName() != null ? user.getName() : "",
                user.getEmail() != null ? user.getEmail() : "",
                user.getSource() != null ? user.getSource() : "",
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : "",
                user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : ""
            };
            csvWriter.writeNext(row);
        }

        csvWriter.close();
        return stringWriter.toString();
    }

    private String formatAsXml(List<UserResponseDto> users) throws Exception {
        UserListWrapper wrapper = new UserListWrapper(users);
        return xmlMapper.writeValueAsString(wrapper);
    }

    public String getContentType(OutputFormat format) {
        return format.getContentType();
    }

    public String getFileExtension(OutputFormat format) {
        switch (format) {
            case JSON:
                return ".json";
            case CSV:
                return ".csv";
            case XML:
                return ".xml";
            default:
                return ".txt";
        }
    }

    public static class UserListWrapper {
        private List<UserResponseDto> users;

        public UserListWrapper() {}

        public UserListWrapper(List<UserResponseDto> users) {
            this.users = users;
        }

        public List<UserResponseDto> getUsers() {
            return users;
        }

        public void setUsers(List<UserResponseDto> users) {
            this.users = users;
        }
    }
}

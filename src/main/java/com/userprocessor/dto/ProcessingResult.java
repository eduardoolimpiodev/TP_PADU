package com.userprocessor.dto;

import java.util.ArrayList;
import java.util.List;

public class ProcessingResult {

    private int totalRecords;
    private int processedRecords;
    private int skippedRecords;
    private int errorRecords;
    private List<String> errors;
    private List<String> warnings;
    private List<UserResponseDto> processedUsers;

    public ProcessingResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.processedUsers = new ArrayList<>();
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getProcessedRecords() {
        return processedRecords;
    }

    public void setProcessedRecords(int processedRecords) {
        this.processedRecords = processedRecords;
    }

    public int getSkippedRecords() {
        return skippedRecords;
    }

    public void setSkippedRecords(int skippedRecords) {
        this.skippedRecords = skippedRecords;
    }

    public int getErrorRecords() {
        return errorRecords;
    }

    public void setErrorRecords(int errorRecords) {
        this.errorRecords = errorRecords;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }

    public List<UserResponseDto> getProcessedUsers() {
        return processedUsers;
    }

    public void setProcessedUsers(List<UserResponseDto> processedUsers) {
        this.processedUsers = processedUsers;
    }

    public void addProcessedUser(UserResponseDto user) {
        this.processedUsers.add(user);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    @Override
    public String toString() {
        return "ProcessingResult{" +
                "totalRecords=" + totalRecords +
                ", processedRecords=" + processedRecords +
                ", skippedRecords=" + skippedRecords +
                ", errorRecords=" + errorRecords +
                ", errorsCount=" + errors.size() +
                ", warningsCount=" + warnings.size() +
                ", processedUsersCount=" + processedUsers.size() +
                '}';
    }
}

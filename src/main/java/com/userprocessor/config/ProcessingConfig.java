package com.userprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.processing")
public class ProcessingConfig {

    private long maxFileSize = 10 * 1024 * 1024;
    private int maxRecordsPerFile = 10000;
    private boolean skipDuplicateEmails = true;
    private boolean validateEmailFormat = true;
    private boolean allowEmptyFields = false;

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public int getMaxRecordsPerFile() {
        return maxRecordsPerFile;
    }

    public void setMaxRecordsPerFile(int maxRecordsPerFile) {
        this.maxRecordsPerFile = maxRecordsPerFile;
    }

    public boolean isSkipDuplicateEmails() {
        return skipDuplicateEmails;
    }

    public void setSkipDuplicateEmails(boolean skipDuplicateEmails) {
        this.skipDuplicateEmails = skipDuplicateEmails;
    }

    public boolean isValidateEmailFormat() {
        return validateEmailFormat;
    }

    public void setValidateEmailFormat(boolean validateEmailFormat) {
        this.validateEmailFormat = validateEmailFormat;
    }

    public boolean isAllowEmptyFields() {
        return allowEmptyFields;
    }

    public void setAllowEmptyFields(boolean allowEmptyFields) {
        this.allowEmptyFields = allowEmptyFields;
    }
}

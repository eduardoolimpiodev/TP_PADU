package com.userprocessor.validation;

import com.userprocessor.enums.FileType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FileTypeValidator implements ConstraintValidator<ValidFileType, String> {

    @Override
    public void initialize(ValidFileType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        
        try {
            FileType.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

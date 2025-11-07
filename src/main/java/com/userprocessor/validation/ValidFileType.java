package com.userprocessor.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileTypeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFileType {
    String message() default "Invalid file type. Supported types: csv, json, xml";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

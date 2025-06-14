package com.example.library.entities.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.beans.Introspector;

public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, Object> {

    private String[] fieldNames;

    @Override
    public void initialize(AtLeastOneField constraintAnnotation) {
        this.fieldNames = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return false;

        try {
            var propertyDescriptors = Introspector.getBeanInfo(value.getClass()).getPropertyDescriptors();

            for (String fieldName : fieldNames) {
                for (var pd : propertyDescriptors) {
                    if (pd.getName().equals(fieldName)) {
                        Object fieldValue = pd.getReadMethod().invoke(value);
                        if (fieldValue != null) {
                            if (fieldValue instanceof String str && !str.isBlank()) {
                                return true;
                            } else if (!(fieldValue instanceof String)) {
                                return true;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }
}
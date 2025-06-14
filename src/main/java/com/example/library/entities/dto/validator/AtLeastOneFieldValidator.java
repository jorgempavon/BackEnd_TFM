package com.example.library.entities.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class AtLeastOneFieldValidator implements ConstraintValidator<AtLeastOneField, Object> {

    private List<String> fieldNames;

    @Override
    public void initialize(AtLeastOneField constraintAnnotation) {
        this.fieldNames = Arrays.asList(constraintAnnotation.fields());
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return false;

        try {
            var propertyDescriptors = Introspector.getBeanInfo(value.getClass()).getPropertyDescriptors();

            return fieldNames.stream()
                    .anyMatch(field -> hasValidValue(field, propertyDescriptors, value));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasValidValue(String fieldName, PropertyDescriptor[] descriptors, Object bean) {
        return Arrays.stream(descriptors)
                .filter(pd -> pd.getName().equals(fieldName))
                .findFirst()
                .map(pd -> readAndValidate(pd, bean))
                .orElse(false);
    }

    private boolean readAndValidate(PropertyDescriptor pd, Object bean) {
        try {
            Method readMethod = pd.getReadMethod();
            if (readMethod == null) return false;

            Object fieldValue = readMethod.invoke(bean);
            if (fieldValue == null) return false;

            if (fieldValue instanceof String str) {
                return !str.isBlank();
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

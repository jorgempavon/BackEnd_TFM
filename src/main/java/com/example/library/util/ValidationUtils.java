package com.example.library.util;

import com.example.library.entities.model.Rule;
import org.springframework.data.jpa.domain.Specification;

public class ValidationUtils {

    private ValidationUtils(){

    }

    public static boolean isValidAndChangedString(String newValue, String oldValue) {
        return newValue != null && !newValue.isEmpty() && !newValue.equals(oldValue);
    }

    public static boolean isValidAndChangedInteger(Integer newValue, Integer oldValue) {
        return newValue != null && !newValue.equals(oldValue);
    }

    public static void buildQueryStringByField(Specification<Rule> spec, String field, String value){
        if (value != null && !value.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%")
            );
        }
    }

    public static void buildQueryIntegerByField(Specification<Rule> spec,String field, Integer value){
        if (value != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThan(root.get(field), value)
            );
        }
    }
}

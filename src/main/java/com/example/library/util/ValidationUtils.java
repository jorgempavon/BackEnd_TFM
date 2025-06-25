package com.example.library.util;

import com.example.library.entities.model.penalty.Penalty;
import com.example.library.entities.model.rule.Rule;
import com.example.library.entities.model.user.User;
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
    public static Specification<Rule> buildQueryStringByField(Specification<Rule> spec, String field, String value) {
        if (value != null && !value.isBlank()) {
            return spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%")
            );
        }
        return spec;
    }

    public static Specification<User> buildQueryUserStringByField(Specification<User> spec, String field, String value) {
        if (value != null && !value.isBlank()) {
            return spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%")
            );
        }
        return spec;
    }

    public static Specification<Rule> buildQueryIntegerByField(Specification<Rule> spec, String field, Integer value) {
        if (value != null) {
            return spec.and((root, query, cb) ->
                    cb.greaterThan(root.get(field), value)
            );
        }
        return spec;
    }

    public static Specification<Penalty> buildQueryBooleanByField(Specification<Penalty> spec, String field, Boolean value) {
        if (value != null) {
            return spec.and((root, query, cb) -> cb.equal(root.get(field), value));
        }
        return spec;
    }

    public static Specification<Penalty> buildQueryLongByField(Specification<Penalty> spec, String field, Long value) {
        if (value != null) {
            return spec.and((root, query, cb) -> cb.equal(root.get(field), value));
        }
        return spec;
    }
}

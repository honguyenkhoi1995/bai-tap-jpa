package com.example.jpa.specification;

import com.example.jpa.entity.Student;
import org.springframework.data.jpa.domain.Specification;

public class StudentSpecification {
    public static Specification<Student> nameContains(String name) {
        return (root, query, cb) ->
            name == null || name.isBlank() ? null :
            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Student> ageFrom(Integer ageFrom) {
        return (root, query, cb) ->
            ageFrom == null ? null : cb.greaterThanOrEqualTo(root.get("age"), ageFrom);
    }

    public static Specification<Student> ageTo(Integer ageTo) {
        return (root, query, cb) ->
            ageTo == null ? null : cb.lessThanOrEqualTo(root.get("age"), ageTo);
    }

    public static Specification<Student> emailEndsWith(String email) {
        return (root, query, cb) ->
            email == null || email.isBlank() ? null :
            cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase());
    }
}

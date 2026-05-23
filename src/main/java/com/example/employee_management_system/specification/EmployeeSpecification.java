package com.example.employee_management_system.specification;

import com.example.employee_management_system.model.Employees;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EmployeeSpecification {

    public static Specification<Employees> hasFirstName(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    public static Specification<Employees> hasLastName(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }

    public static Specification<Employees> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<Employees> hasDepartment(String department) {
        return (root, query, cb) -> {
            if (department == null || department.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("department")), "%" + department.toLowerCase() + "%");
        };
    }

    public static Specification<Employees> hasJobTitle(String jobTitle) {
        return (root, query, cb) -> {
            if (jobTitle == null || jobTitle.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("jobTitle")), "%" + jobTitle.toLowerCase() + "%");
        };
    }

    public static Specification<Employees> hasPhoneNumber(String phoneNumber) {
        return (root, query, cb) -> {
            if (phoneNumber == null || phoneNumber.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(root.get("phoneNumber"), "%" + phoneNumber + "%");
        };
    }

    public static Specification<Employees> hasSalaryGreaterThan(Float minSalary) {
        return (root, query, cb) -> {
            if (minSalary == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("salary"), minSalary);
        };
    }

    public static Specification<Employees> hasSalaryLessThan(Float maxSalary) {
        return (root, query, cb) -> {
            if (maxSalary == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("salary"), maxSalary);
        };
    }

    public static Specification<Employees> hiredAfter(LocalDate hireDateFrom) {
        return (root, query, cb) -> {
            if (hireDateFrom == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("hireDate"), hireDateFrom);
        };
    }

    public static Specification<Employees> hiredBefore(LocalDate hireDateTo) {
        return (root, query, cb) -> {
            if (hireDateTo == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("hireDate"), hireDateTo);
        };
    }
    /// By me
    ///So my specification builder is making a dynamic query work by dynamically building a specification which is a specific tailor-out for every request.
    /// without it, I would have to write a query for every possible combination of filters.
    public static Specification<Employees> buildSpecification(
            String firstName,
            String lastName,
            String email,
            String department,
            String jobTitle,
            String phoneNumber,
            Float minSalary,
            Float maxSalary,
            LocalDate hireDateFrom,
            LocalDate hireDateTo) {
        
        Specification<Employees> spec = Specification.where((Specification<Employees>) null);
        
        if (firstName != null && !firstName.isEmpty()) {
            spec = spec.and(hasFirstName(firstName));
        }
        if (lastName != null && !lastName.isEmpty()) {
            spec = spec.and(hasLastName(lastName));
        }
        if (email != null && !email.isEmpty()) {
            spec = spec.and(hasEmail(email));
        }
        if (department != null && !department.isEmpty()) {
            spec = spec.and(hasDepartment(department));
        }
        if (jobTitle != null && !jobTitle.isEmpty()) {
            spec = spec.and(hasJobTitle(jobTitle));
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            spec = spec.and(hasPhoneNumber(phoneNumber));
        }
        if (minSalary != null) {
            spec = spec.and(hasSalaryGreaterThan(minSalary));
        }
        if (maxSalary != null) {
            spec = spec.and(hasSalaryLessThan(maxSalary));
        }
        if (hireDateFrom != null) {
            spec = spec.and(hiredAfter(hireDateFrom));
        }
        if (hireDateTo != null) {
            spec = spec.and(hiredBefore(hireDateTo));
        }
        
        return spec;
    }
}

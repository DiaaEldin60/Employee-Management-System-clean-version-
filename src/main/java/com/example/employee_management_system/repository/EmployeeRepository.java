package com.example.employee_management_system.repository;

import com.example.employee_management_system.model.Employees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
/// by me
/// Repository interface extends JpaRepository and JpaSpecificationExecutor.
/// JpaRepository provides basic CRUD operations.
/// JpaSpecificationExecutor provides advanced query capabilities for filtering.
public interface EmployeeRepository extends JpaRepository<Employees,Integer>, JpaSpecificationExecutor<Employees> {

    Optional<Employees> findByEmail(String email);

    List<Employees> findByDepartment(String department);

    Optional<Employees> findByUserId(Integer userId);


    /// by me
    /// these queries are used to retrieve aggregate data about departments.
    /// Query annotation is used to define custom queries.
    @Query("SELECT COUNT(DISTINCT e.department) FROM Employees e")
    long countDistinctDepartments();

    @Query("SELECT e.department, COUNT(e) FROM Employees e GROUP BY e.department")
    List<Object[]> countEmployeesByDepartment();
}

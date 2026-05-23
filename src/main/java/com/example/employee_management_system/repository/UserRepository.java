package com.example.employee_management_system.repository;

import com.example.employee_management_system.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,Integer> {

    Optional<Users> findByUserName(String userName);

    @Query("SELECT u FROM Users u JOIN u.employees e WHERE e.email = :email")
    Optional<Users> findByEmployeeEmail(@Param("email") String email);

    long countByEnabled(boolean enabled);

    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.employees")
    List<Users> findAllWithEmployees();
}

package com.example.employee_management_system.repository;

import com.example.employee_management_system.model.Authorities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authorities, Integer> {

    Optional<Authorities> findByName(String name);
}

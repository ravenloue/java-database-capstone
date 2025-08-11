package com.project.back_end.repo;

import com.project.back_end.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Admin model.
 * Supports basic CRUD operations and custom queries using Spring Data JPA.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByUsername(String username);
}
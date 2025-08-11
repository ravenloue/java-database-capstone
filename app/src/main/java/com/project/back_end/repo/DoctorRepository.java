package com.project.back_end.repo;

import com.project.back_end.models.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for the Doctor entity.
 * Provides basic CRUD operations and custom queries for doctor search and filtering.
 */
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Doctor findByEmail(String email);

    @Query("SELECT d FROM Doctor d WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(String name);

    @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
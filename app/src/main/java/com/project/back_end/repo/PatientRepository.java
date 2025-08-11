package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for the Patient entity.
 * Provides CRUD operations and custom queries using Spring Data JPA.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Patient findByEmail(String email);

    Patient findByEmailOrPhone(String email, String phone);
}
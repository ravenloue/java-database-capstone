package com.project.back_end.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.back_end.models.Prescription;

/**
 * Repository interface for the Prescription model.
 * Enables CRUD operations and custom queries in MongoDB using Spring Data.
 */
@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {

    List<Prescription> findByAppointmentId(Long appointmentId);
}
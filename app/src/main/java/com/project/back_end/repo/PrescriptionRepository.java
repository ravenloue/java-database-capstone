package com.project.back_end.repo;

import com.project.back_end.models.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for the Prescription model.
 * Enables CRUD operations and custom queries in MongoDB using Spring Data.
 */
@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    List<Prescription> findByAppointmentId(Long appointmentId);
}
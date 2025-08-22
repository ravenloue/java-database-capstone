package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Appointment entity.
 * Provides basic CRUD and custom queries for managing appointment data.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a " +
           "JOIN a.doctor d WHERE d.id = :doctorId " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
        @Param("doctorId") Long doctorId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT a FROM Appointment a " + "LEFT JOIN FETCH a.patient p " + 
           "LEFT JOIN FETCH a.doctor d " + "WHERE d.id = :doctorId " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            @Param("doctorId") Long doctorId,
            @Param("patientName") String patientName,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
       Long patientId, int status);

    @Query("SELECT a FROM Appointment a " + "JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(
        @Param("doctorName") String doctorName,
        @Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a " + "JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId " + "AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
        @Param("doctorName") String doctorName,
        @Param("patientId") Long patientId,
        @Param("status") int status);

    @Modifying
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(int status,long id);

    // AppointmentRepository.java
    @Query("SELECT a FROM Appointment a " + "WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentTime >= :now ORDER BY a.appointmentTime ASC")
    List<Appointment> findUpcomingByDoctor(@Param("doctorId") Long doctorId,
        @Param("now") java.time.LocalDateTime now);

    @Query("SELECT a FROM Appointment a " + "WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentTime >= :now AND LOWER(a.patient.name) LIKE " + 
           "LOWER(CONCAT('%', :pname, '%')) ORDER BY a.appointmentTime ASC")
    List<Appointment> findUpcomingByDoctorAndPatient(
        @Param("doctorId") Long doctorId,
        @Param("now") java.time.LocalDateTime now,
        @Param("pname") String pname);

}
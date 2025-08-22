package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;

/**
 * Service class for managing appointment operations.
 * 
 * Handles appointment booking, updates, cancellations, and retrieval. 
 * Integrates with repositories for data persistence and token service
 * for authentication. Provides business logic validation for appointments.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final com.project.back_end.services.Service service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
            com.project.back_end.services.Service service, TokenService tokenService,
            PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

     /**
     * Books a new appointment in the system.
     * 
     * Persists appointment data to database. Returns success/failure status
     * for operation. Logs any exceptions that occur during save operation.
     * 
     * @param appointment Appointment entity with patient, doctor, and time
     * @return 1 for success, 0 for failure
     */
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return 0;
        }
    }

    /**
     * Updates existing appointment details.
     * 
     * Validates appointment exists and patient ID matches before updating.
     * Checks doctor availability and time conflicts. Returns appropriate
     * HTTP response with status message.
     * 
     * @param appointment Updated appointment data including ID
     * @return ResponseEntity with status code and message
     */
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> result = appointmentRepository.findById(appointment.getId());

        if (!result.isPresent()) {
            response.put("message", "No appointment available with id: " + appointment.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (result.get().getPatient().getId() != appointment.getPatient().getId()) {
            response.put("message", "Patient Id mismatch");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        int out = service.validateAppointment(appointment);
        if (out == 1) {
            try {
                appointmentRepository.save(appointment);
                response.put("message", "Appointment Updated Successfully");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } catch (Exception e) {
                System.out.println("Error: " + e);
                response.put("message", "Internal Server Error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else if (out == -1) {
            response.put("message", "Invalid doctor id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "Appointment already booked for given time or Doctor not available");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Cancels appointment by ID with patient verification.
     * 
     * Extracts patient from token and verifies ownership before deletion.
     * Ensures only the patient who booked can cancel their appointment.
     * Returns appropriate status based on operation result.
     * 
     * @param id Appointment ID to cancel
     * @param token JWT token for patient authentication
     * @return ResponseEntity with deletion status and message
     */
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        String extractedToken = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(extractedToken);

        if (patient.getId() != appointment.get().getPatient().getId()) {
            response.put("message", "Patient Id mismatch");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (appointment.isPresent()) {
            try {
                appointmentRepository.delete(appointment.get());
                response.put("message", "Appointment Deleted Successfully");
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } catch (Exception e) {
                System.out.println("Error: " + e);
                response.put("message", "Internal Server Error");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        }
        response.put("message", "No appointment for given id: " + id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Retrieves appointments for a doctor filtered by date and patient name.
     * 
     * Extracts doctor ID from token for security. Filters appointments by
     * date range (full day). Optional patient name filter with partial match.
     * Returns DTOs with patient details for display.
     * 
     * @param pname Patient name filter or "null" for all patients
     * @param date Date to filter appointments (uses full day range)
     * @param token JWT token containing doctor email
     * @return Map containing list of appointment DTOs
     */
    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> map = new HashMap<>();
        String extractedEmail = tokenService.extractEmail(token);
        Long doctorId = doctorRepository.findByEmail(extractedEmail).getId();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<Appointment> appointments;

        if (pname.equals("null")) {
            appointments = appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);
        } else {
            appointments = appointmentRepository
                    .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                            doctorId, pname, startOfDay, endOfDay);
        }

        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(app -> new AppointmentDTO(
                        app.getId(),
                        app.getDoctor().getId(),
                        app.getDoctor().getName(),
                        app.getPatient().getId(),
                        app.getPatient().getName(),
                        app.getPatient().getEmail(),
                        app.getPatient().getPhone(),
                        app.getPatient().getAddress(),
                        app.getApptTime(),
                        app.getStatus()))
                .collect(Collectors.toList());

        map.put("appointments", appointmentDTOs);
        return map;
    }

    /**
     * Updates appointment status to completed.
     * 
     * Changes status from 0 (pending) to 1 (completed) for given appointment.
     * Used when doctor marks appointment as finished.
     * 
     * @param appointmentId ID of appointment to update
     */
    @Transactional
    public void changeStatus(long appointmentId)
    {
        appointmentRepository.updateStatus(1, appointmentId);
    }

    @Transactional
    public Map<String, Object> getUpcomingAppointments(String pname, String token) {
        Map<String, Object> map = new HashMap<>();

        String email = tokenService.extractEmail(token);
        Long doctorId = doctorRepository.findByEmail(email).getId();

        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        List<Appointment> appointments;
        if (pname == null || pname.isBlank() || "null".equalsIgnoreCase(pname)) {
            appointments = appointmentRepository.findUpcomingByDoctor(doctorId, now);
        } else {
            appointments = appointmentRepository.findUpcomingByDoctorAndPatient(doctorId, now, pname);
        }

        List<AppointmentDTO> rows = appointments.stream()
            .map(app -> new AppointmentDTO(
                app.getId(),
                app.getDoctor().getId(),
                app.getDoctor().getName(),
                app.getPatient().getId(),
                app.getPatient().getName(),
                app.getPatient().getEmail(),
                app.getPatient().getPhone(),
                app.getPatient().getAddress(),
                app.getApptTime(),      // or getAppointmentTime()
                app.getStatus()))
            .toList();

        map.put("appointments", rows);
        return map;
    }

}
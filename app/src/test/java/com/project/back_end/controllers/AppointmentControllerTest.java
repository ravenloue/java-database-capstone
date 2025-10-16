package com.project.back_end.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;

/**
 * Unit tests for AppointmentController REST endpoints.
 * 
 * Comprehensive test suite covering appointment CRUD operations, token
 * validation, and various edge cases. Tests isolated controller behavior
 * without database or service layer dependencies.
 */
@WebMvcTest(AppointmentController.class)
@TestPropertySource(properties = {
    "api.path=/",
    "spring.jpa.hibernate.ddl-auto=none"
})
class AppointmentControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;



    private Appointment testAppointment;
    private Map<String, String> validTokenResponse;
    private Map<String, String> invalidTokenResponse;
    private Map<String, Object> appointmentResponse;

    /**
     * Initializes test fixtures before each test execution.
     * 
     * Sets up test appointment, doctor, patient, and response objects
     * for consistent test data. Configures ObjectMapper with JavaTimeModule
     * for LocalDateTime serialization.
     */
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Smith");

        Patient patient = new Patient();
        patient.setId(1L);
        patient.setName("John Doe");

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setDoctor(doctor);
        testAppointment.setPatient(patient);
        testAppointment.setApptTime(LocalDateTime.now().plusDays(1));
        testAppointment.setStatus(0);

        validTokenResponse = new HashMap<>();
        validTokenResponse.put("valid", "true");

        invalidTokenResponse = new HashMap<>();
        invalidTokenResponse.put("error", "Invalid or expired token");

        appointmentResponse = new HashMap<>();
        appointmentResponse.put("appointments", new Object[]{});
    }

    /**
     * Tests retrieval of appointments for a specific date with valid token.
     * 
     * Verifies successful appointment retrieval when doctor token is valid
     * and returns 200 OK with appointment list.
     */
    @Test
    void getAppointments_ValidToken_ReturnsAppointments() throws Exception {
        when(service.validateToken("valid-token", "doctor"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(appointmentService.getAppointment(anyString(), any(LocalDate.class), 
                anyString()))
            .thenReturn(appointmentResponse);

        mockMvc.perform(get("/appointments/2024-01-15/null/valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appointments").exists());
    }

    /**
     * Tests appointment retrieval with invalid token.
     * 
     * Verifies that invalid token returns 401 Unauthorized with error
     * message and no appointment data is exposed.
     */
    @Test
    void getAppointments_InvalidToken_ReturnsUnauthorized() throws Exception {
        when(service.validateToken("invalid-token", "doctor"))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(invalidTokenResponse));

        mockMvc.perform(get("/appointments/2024-01-15/null/invalid-token"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error")
                .value("Invalid or expired token"));
    }

    /**
     * Tests appointment retrieval with patient name filter.
     * 
     * Verifies filtering functionality when patient name is provided
     * in the request path parameters.
     */
    @Test
    void getAppointments_WithPatientFilter_ReturnsFilteredResults() throws Exception {
        when(service.validateToken("valid-token", "doctor"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(appointmentService.getAppointment(eq("John"), any(LocalDate.class), 
                eq("valid-token")))
            .thenReturn(appointmentResponse);

        mockMvc.perform(get("/appointments/2024-01-15/John/valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appointments").exists());
    }

    /**
     * Tests retrieval of upcoming appointments without filter.
     * 
     * Verifies endpoint returns all upcoming appointments when no
     * patient filter is applied.
     */
    @Test
    void getUpcomingAppointments_NoFilter_ReturnsAllUpcoming() 
            throws Exception {
        when(service.validateToken("valid-token", "doctor"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(appointmentService.getUpcomingAppointments("null", "valid-token"))
            .thenReturn(appointmentResponse);

        mockMvc.perform(get("/appointments/upcoming/valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appointments").exists());
    }

    /**
     * Tests upcoming appointments with patient name filter.
     * 
     * Verifies filtered upcoming appointments are returned when
     * patient name is specified.
     */
    @Test
    void getUpcomingAppointments_WithFilter_ReturnsFiltered() throws Exception {
        when(service.validateToken("valid-token", "doctor"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(appointmentService.getUpcomingAppointments("John", "valid-token"))
            .thenReturn(appointmentResponse);

        mockMvc.perform(get("/appointments/upcoming/John/valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appointments").exists());
    }

    /**
     * Tests successful appointment booking with valid data and token.
     * 
     * Verifies appointment creation returns 201 Created status with
     * success message when all validations pass.
     */
    @Test
    void bookAppointment_ValidData_ReturnsCreated() throws Exception {
        Map<String, String> tokenBody = Map.of("valid", "true");
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(tokenBody));
        when(service.validateAppointment(any(Appointment.class)))
            .thenReturn(1);
        when(appointmentService.bookAppointment(any(Appointment.class)))
            .thenReturn(1);

        mockMvc.perform(post("/appointments/valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAppointment)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message")
                .value("Appointment Booked Successfully"));
    }

    /**
     * Tests appointment booking with invalid doctor ID.
     * 
     * Verifies proper error handling when specified doctor doesn't
     * exist in the system.
     */
    @Test
    void bookAppointment_InvalidDoctor_ReturnsBadRequest() throws Exception {
        Map<String, String> tokenBody = Map.of("valid", "true");
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(tokenBody));
        when(service.validateAppointment(any(Appointment.class)))
            .thenReturn(-1);

        mockMvc.perform(post("/appointments/valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAppointment)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Invalid doctor id"));
    }

    /**
     * Tests appointment booking when time slot is unavailable.
     * 
     * Verifies conflict detection when appointment slot is already
     * booked or doctor is unavailable.
     */
    @Test
    void bookAppointment_TimeConflict_ReturnsBadRequest() throws Exception {
        Map<String, String> tokenBody = Map.of("valid", "true");
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(tokenBody));
        when(service.validateAppointment(any(Appointment.class)))
            .thenReturn(0);

        mockMvc.perform(post("/appointments/valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAppointment)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value("Appointment already booked for given time or " +
                       "Doctor not available"));
    }

    /**
     * Tests appointment update with valid data and authorization.
     * 
     * Verifies successful appointment modification returns 200 OK
     * with appropriate success message.
     */
    @Test
    void updateAppointment_ValidData_ReturnsOk() throws Exception {
        Map<String, String> tokenBody = Map.of("valid", "true");
        Map<String, String> updateResponse = Map.of(
            "message", 
            "Appointment Updated Successfully"
        );
        
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(tokenBody));
        when(appointmentService.updateAppointment(any(Appointment.class)))
            .thenReturn(ResponseEntity.ok(updateResponse));

        mockMvc.perform(put("/appointments/valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAppointment)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message")
                .value("Appointment Updated Successfully"));
    }

    /**
     * Tests appointment cancellation with valid authorization.
     * 
     * Verifies successful deletion returns appropriate status and
     * confirmation message.
     */
    @Test
    void cancelAppointment_ValidToken_ReturnsOk() throws Exception {
        Map<String, String> tokenBody = Map.of("valid", "true");
        Map<String, String> cancelResponse = Map.of(
            "message", 
            "Appointment Deleted Successfully"
        );
        
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(tokenBody));
        when(appointmentService.cancelAppointment(1L, "valid-token"))
            .thenReturn(ResponseEntity.ok(cancelResponse));

        mockMvc.perform(delete("/appointments/1/valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message")
                .value("Appointment Deleted Successfully"));
    }

    /**
     * Tests appointment cancellation for non-existent appointment.
     * 
     * Verifies proper error handling when attempting to cancel an
     * appointment that doesn't exist.
     */
    @Test
    void cancelAppointment_NotFound_ReturnsNotFound() throws Exception {
        Map<String, String> tokenBody = Map.of("valid", "true");
        Map<String, String> notFoundResponse = Map.of(
            "message", 
            "No appointment for given id: 999"
        );
        
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(tokenBody));
        when(appointmentService.cancelAppointment(999L, "valid-token"))
            .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(notFoundResponse));

        mockMvc.perform(delete("/appointments/999/valid-token"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message")
                .value("No appointment for given id: 999"));
    }
}
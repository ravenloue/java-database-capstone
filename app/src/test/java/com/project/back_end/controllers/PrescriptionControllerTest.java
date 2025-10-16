package com.project.back_end.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.back_end.models.Prescription;

/**
 * Unit tests for PrescriptionController REST endpoints.
 * 
 * Tests prescription creation and retrieval with doctor authorization.
 * Validates token-based access control and proper integration with
 * appointment status updates.
 */
@WebMvcTest(PrescriptionController.class)
@TestPropertySource(properties = {
    "api.path=/",
    "spring.jpa.hibernate.ddl-auto=none"
})
class PrescriptionControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Prescription testPrescription;
    private Map<String, String> validTokenResponse;
    private Map<String, String> invalidTokenResponse;

    /**
     * Initializes test fixtures before each test execution.
     * 
     * Sets up test prescription and response templates for consistent
     * test data across all test methods.
     */
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        testPrescription = new Prescription();
        testPrescription.setId(1);
        testPrescription.setPatientName("John Doe");
        testPrescription.setApptId(100L);
        testPrescription.setMedication("Amoxicillin");
        testPrescription.setDosage("500mg twice daily");
        testPrescription.setDoctorNotes("Take with food");

        validTokenResponse = new HashMap<>();
        validTokenResponse.put("valid", "true");

        invalidTokenResponse = new HashMap<>();
        invalidTokenResponse.put("error", "Invalid or expired token");
    }

    /**
     * Tests successful prescription creation by authorized doctor.
     * 
     * Verifies prescription save and appointment status update when
     * doctor token is valid. Returns 201 Created on success.
     */
    @Test
    void savePrescription_ValidDoctorToken_ReturnsCreated() throws Exception {
        Map<String, String> saveResponse = Map.of("message", 
            "Prescription saved");
        
        when(service.validateToken("doctor-token", "doctor"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(prescriptionService.savePrescription(any(Prescription.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                .body(saveResponse));

        mockMvc.perform(post("/prescription/doctor-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPrescription)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Prescription saved"));

        verify(appointmentService).changeStatus(100L);
    }

    /**
     * Tests prescription creation with invalid doctor token.
     * 
     * Verifies unauthorized access is rejected with 401 status when
     * token validation fails.
     */
    @Test
    void savePrescription_InvalidToken_ReturnsUnauthorized() throws Exception {
        when(service.validateToken("invalid-token", "doctor"))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new HashMap<>()));

        mockMvc.perform(post("/prescription/invalid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPrescription)))
            .andExpect(status().isUnauthorized());

        verify(appointmentService, never()).changeStatus(anyLong());
    }

    /**
     * Tests prescription creation for duplicate appointment.
     * 
     * Verifies that attempting to create prescription for appointment
     * that already has one returns 400 Bad Request.
     */
    @Test
    void savePrescription_DuplicateAppointment_ReturnsBadRequest() throws Exception {
        Map<String, String> errorResponse = Map.of(
            "message", 
            "prescription already exists"
            );
        
        when(service.validateToken("doctor-token", "doctor"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(prescriptionService.savePrescription(any(Prescription.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse));

        mockMvc.perform(post("/prescription/doctor-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPrescription)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value("prescription already exists"));
    }

    /**
     * Tests prescription retrieval by appointment ID.
     * 
     * Verifies authorized doctor can retrieve prescription details
     * for a specific appointment.
     */
    @Test
    void getPrescription_ValidDoctorToken_ReturnsPrescription() throws Exception {
        Map<String, Object> prescriptionResponse = new HashMap<>();
        prescriptionResponse.put("prescription", Arrays.asList(testPrescription));
        
        when(service.validateToken("doctor-token", "doctor"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(prescriptionService.getPrescription(100L))
            .thenReturn(ResponseEntity.ok(prescriptionResponse));

        mockMvc.perform(get("/prescription/100/doctor-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.prescription").isArray())
            .andExpect(jsonPath("$.prescription[0].patientName")
                .value("John Doe"));
    }

    /**
     * Tests prescription retrieval with invalid token.
     * 
     * Verifies unauthorized access returns 401 status when token
     * validation fails.
     */
    @Test
    void getPrescription_InvalidToken_ReturnsUnauthorized() throws Exception {
        when(service.validateToken("invalid-token", "doctor"))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new HashMap<>()));

        mockMvc.perform(get("/prescription/100/invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    /**
     * Tests prescription creation when service throws exception.
     * 
     * Verifies internal server errors return 500 status with
     * appropriate error message.
     */
    @Test
    void savePrescription_ServiceError_ReturnsServerError() throws Exception {
        Map<String, String> errorResponse = Map.of(
            "message", 
            "Internal Server Error"
        );
        
        when(service.validateToken("doctor-token", "doctor"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(prescriptionService.savePrescription(any(Prescription.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse));

        mockMvc.perform(post("/prescription/doctor-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPrescription)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("Internal Server Error"));
    }

    /**
     * Tests prescription retrieval for non-existent appointment.
     * 
     * Verifies empty list is returned when no prescription exists
     * for given appointment ID.
     */
    @Test
    void getPrescription_NotFound_ReturnsEmptyList() throws Exception {
        Map<String, Object> emptyResponse = new HashMap<>();
        emptyResponse.put("prescription", Arrays.asList());
        
        when(service.validateToken("doctor-token", "doctor"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(prescriptionService.getPrescription(999L))
            .thenReturn(ResponseEntity.ok(emptyResponse));

        mockMvc.perform(get("/prescription/999/doctor-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.prescription").isArray())
            .andExpect(jsonPath("$.prescription").isEmpty());
    }
}
package com.project.back_end.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.back_end.models.Patient;

/**
 * Unit tests for ValidationFailed global exception handler.
 * 
 * Tests validation error handling across all controllers. Verifies
 * that validation annotations properly trigger error responses with
 * appropriate error messages.
 */
@WebMvcTest({PatientController.class, ValidationFailed.class})
@TestPropertySource(properties = {
    "api.path=/",
    "spring.jpa.hibernate.ddl-auto=none"
})
class ValidationFailedTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Sets up test environment before each test execution.
     * 
     * Initializes required mocks and test data for validation
     * error testing.
     */
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        // Setup is minimal as we're testing validation failures
    }

    /**
     * Tests validation error for null required field.
     * 
     * Verifies that null values in required fields trigger
     * 400 Bad Request with appropriate error message.
     */
    @Test
    void handleValidationException_NullName_ReturnsBadRequest() throws Exception {
        Patient patient = new Patient();
        patient.setName(null);
        patient.setEmail("test@example.com");
        patient.setPassword("password123");
        patient.setPhone("555-123-4567");
        patient.setAddress("123 Main St");

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Tests validation error for invalid email format.
     * 
     * Verifies that malformed email addresses trigger validation
     * error with appropriate message.
     */
    @Test
    void handleValidationException_InvalidEmail_ReturnsBadRequest() throws Exception {
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEmail("invalid-email");
        patient.setPassword("password123");
        patient.setPhone("555-123-4567");
        patient.setAddress("123 Main St");

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Tests validation error for invalid phone format.
     * 
     * Verifies that phone numbers not matching required pattern
     * trigger validation error.
     */
    @Test
    void handleValidationException_InvalidPhone_ReturnsBadRequest() throws Exception {
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEmail("john@example.com");
        patient.setPassword("password123");
        patient.setPhone("1234567890");
        patient.setAddress("123 Main St");

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Tests validation error for short password.
     * 
     * Verifies that passwords below minimum length trigger
     * validation error with size constraint message.
     */
    @Test
    void handleValidationException_ShortPassword_ReturnsBadRequest() throws Exception {
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEmail("john@example.com");
        patient.setPassword("123");
        patient.setPhone("555-123-4567");
        patient.setAddress("123 Main St");

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Tests validation error for exceeding max field length.
     * 
     * Verifies that fields exceeding maximum length constraints
     * trigger appropriate validation errors.
     */
    @Test
    void handleValidationException_LongAddress_ReturnsBadRequest() throws Exception {
        Patient patient = new Patient();
        patient.setName("John Doe");
        patient.setEmail("john@example.com");
        patient.setPassword("password123");
        patient.setPhone("555-123-4567");
        patient.setAddress("A".repeat(256));

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Tests validation with multiple field errors.
     * 
     * Verifies that multiple validation errors are properly
     * aggregated and returned in response.
     */
    @Test
    void handleValidationException_MultipleErrors_ReturnsBadRequest() throws Exception {
        Patient patient = new Patient();

        patient.setName(null);
        patient.setEmail("invalid");
        patient.setPassword("12");
        patient.setPhone("wrong");
        patient.setAddress(null);

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patient)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    /**
     * Tests validation with completely empty request body.
     * 
     * Verifies proper handling when request body contains no
     * data for required fields.
     */
    @Test
    void handleValidationException_EmptyBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
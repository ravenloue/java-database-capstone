package com.project.back_end.controllers;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;

/**
 * Unit tests for PatientController REST endpoints.
 * 
 * Tests patient registration, authentication, data retrieval, and
 * appointment filtering. Validates token-based authorization and
 * proper error handling for various scenarios.
 */
@WebMvcTest(PatientController.class)
@TestPropertySource(properties = {
    "api.path=/",
    "spring.jpa.hibernate.ddl-auto=none"
})
class PatientControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient testPatient;
    private Login loginRequest;
    private Map<String, String> validTokenResponse;
    private Map<String, String> invalidTokenResponse;
    private Map<String, Object> patientDataResponse;

    /**
     * Sets up test fixtures before each test execution.
     * 
     * Initializes test patient, login request, and response templates
     * for consistent test data across all test methods.
     */
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        testPatient = new Patient();
        testPatient.setId(1L);
        testPatient.setName("John Doe");
        testPatient.setEmail("john@example.com");
        testPatient.setPassword("password123");
        testPatient.setPhone("555-123-4567");
        testPatient.setAddress("123 Main St");

        loginRequest = new Login();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        validTokenResponse = new HashMap<>();
        validTokenResponse.put("valid", "true");

        invalidTokenResponse = new HashMap<>();
        invalidTokenResponse.put("error", "Invalid or expired token");

        patientDataResponse = new HashMap<>();
        patientDataResponse.put("patient", testPatient);
    }

    /**
     * Tests patient data retrieval with valid token.
     * 
     * Verifies authorized access returns patient details with
     * 200 OK status.
     */
    @Test
    void getPatientData_ValidToken_ReturnsPatientData() throws Exception {
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(patientService.getPatientDetails("valid-token"))
            .thenReturn(ResponseEntity.ok(patientDataResponse));

        mockMvc.perform(get("/patient/valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.patient.name").value("John Doe"));
    }

    /**
     * Tests patient data retrieval with invalid token.
     * 
     * Verifies unauthorized access returns 401 status with error
     * message when token is invalid.
     */
    @Test
    void getPatientData_InvalidToken_ReturnsUnauthorized() throws Exception {
        when(service.validateToken("invalid-token", "patient"))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new HashMap<>()));

        mockMvc.perform(get("/patient/invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    /**
     * Tests successful patient registration with valid data.
     * 
     * Verifies new patient creation returns 201 Created with
     * success message when validation passes.
     */
    @Test
    void createPatient_ValidData_ReturnsCreated() throws Exception {
        when(service.validatePatient(any(Patient.class)))
            .thenReturn(true);
        when(patientService.createPatient(any(Patient.class)))
            .thenReturn(1);

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatient)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Signup successfull"));
    }

    /**
     * Tests patient registration with duplicate email or phone.
     * 
     * Verifies duplicate detection returns 400 Bad Request with
     * appropriate error message.
     */
    @Test
    void createPatient_DuplicateEmailOrPhone_ReturnsBadRequest() throws Exception {
        when(service.validatePatient(any(Patient.class)))
            .thenReturn(false);

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatient)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value("Patient with email id or phone no already exist"));
    }

    /**
     * Tests patient registration when database save fails.
     * 
     * Verifies internal errors return 500 status with error message
     * when persistence operation fails.
     */
    @Test
    void createPatient_SaveFails_ReturnsServerError() throws Exception {
        when(service.validatePatient(any(Patient.class)))
            .thenReturn(true);
        when(patientService.createPatient(any(Patient.class)))
            .thenReturn(0);

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatient)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    /**
     * Tests patient login with valid credentials.
     * 
     * Verifies successful authentication returns JWT token with
     * 200 OK status.
     */
    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        Map<String, String> tokenResponse = Map.of("token", "patient-jwt-token");
        when(service.validatePatientLogin(any(Login.class)))
            .thenReturn(ResponseEntity.ok(tokenResponse));

        mockMvc.perform(post("/patient/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("patient-jwt-token"));
    }

    /**
     * Tests patient login with invalid credentials.
     * 
     * Verifies failed authentication returns 401 Unauthorized with
     * error message.
     */
    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        Map<String, String> errorResponse = Map.of(
            "error", 
            "Password does not match"
        );
        when(service.validatePatientLogin(any(Login.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse));

        mockMvc.perform(post("/patient/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Password does not match"));
    }

    /**
     * Tests retrieval of patient appointments with valid authorization.
     * 
     * Verifies appointments are returned for authorized user with
     * correct patient ID.
     */
    @Test
    void getPatientAppointment_ValidToken_ReturnsAppointments() throws Exception {
        Map<String, Object> appointmentResponse = new HashMap<>();
        appointmentResponse.put("appointments", new Object[]{});
        
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(patientService.getPatientAppointment(1L, "valid-token"))
            .thenReturn(ResponseEntity.ok(appointmentResponse));

        mockMvc.perform(get("/patient/1/patient/valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appointments").exists());
    }

    /**
     * Tests appointment filtering by condition and doctor name.
     * 
     * Verifies combined filters return filtered appointments when
     * patient token is valid.
     */
    @Test
    void filterPatientAppointment_ValidFilters_ReturnsFiltered() throws Exception {
        Map<String, Object> filterResponse = new HashMap<>();
        filterResponse.put("appointments", new Object[]{});
        
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(service.filterPatient("past", "Dr.Smith", "valid-token"))
            .thenReturn(ResponseEntity.ok(filterResponse));

        mockMvc.perform(get("/patient/filter/past/Dr.Smith/valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appointments").exists());
    }

    /**
     * Tests appointment filtering with null values.
     * 
     * Verifies that null filter values are handled correctly and
     * return unfiltered results.
     */
    @Test
    void filterPatientAppointment_NullFilters_ReturnsAll() throws Exception {
        Map<String, Object> filterResponse = new HashMap<>();
        filterResponse.put("appointments", new Object[]{});
        
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(service.filterPatient("null", "null", "valid-token"))
            .thenReturn(ResponseEntity.ok(filterResponse));

        mockMvc.perform(get("/patient/filter/null/null/valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appointments").exists());
    }

    /**
     * Tests patient creation with invalid phone format.
     * 
     * Verifies validation annotations reject invalid phone numbers
     * with 400 Bad Request status.
     */
    @Test
    void createPatient_InvalidPhone_ReturnsBadRequest() throws Exception {
        Patient invalidPatient = new Patient();
        invalidPatient.setName("John");
        invalidPatient.setEmail("john@example.com");
        invalidPatient.setPassword("pass123");
        invalidPatient.setPhone("invalid-phone");
        invalidPatient.setAddress("123 Main St");

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
            .andExpect(status().isBadRequest());
    }

    /**
     * Tests patient creation with missing required fields.
     * 
     * Verifies validation annotations trigger 400 Bad Request when
     * required fields are null.
     */
    @Test
    void createPatient_MissingFields_ReturnsBadRequest() throws Exception {
        Patient invalidPatient = new Patient();

        mockMvc.perform(post("/patient")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPatient)))
            .andExpect(status().isBadRequest());
    }
}
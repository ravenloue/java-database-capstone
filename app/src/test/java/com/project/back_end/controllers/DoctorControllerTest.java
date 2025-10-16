package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.back_end.DTO.DoctorUpdateDTO;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;

/**
 * Unit tests for DoctorController REST endpoints.
 * 
 * Tests doctor CRUD operations, authentication, availability checks,
 * and filtering functionality. Validates both public and authenticated
 * endpoints with proper authorization checks.
 */
@WebMvcTest(DoctorController.class)
@TestPropertySource(properties = {
    "api.path=/",
    "spring.jpa.hibernate.ddl-auto=none"
})
@SuppressWarnings("removal")
class DoctorControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    @SuppressWarnings("unused")
    private AdminController adminController;

    private Doctor testDoctor;
    private DoctorUpdateDTO updateDTO;
    private Login loginRequest;
    private Map<String, String> validTokenResponse;
    private Map<String, String> invalidTokenResponse;

    /**
     * Initializes test data before each test execution.
     * 
     * Creates test doctor, update DTO, login request, and response
     * templates for consistent testing across all test methods.
     */
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Smith");
        testDoctor.setSpecialty("Cardiology");
        testDoctor.setEmail("smith@hospital.com");
        testDoctor.setPassword("secure123");
        testDoctor.setPhone("555-123-4567");
        testDoctor.setAvailability(Arrays.asList("09:00-10:00", "14:00-15:00"));

        updateDTO = new DoctorUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setName("Dr. Smith Updated");
        updateDTO.setSpecialty("Cardiology");
        updateDTO.setEmail("smith@hospital.com");
        updateDTO.setPhone("555-123-4567");
        updateDTO.setAvailability(Arrays.asList("09:00-10:00"));

        loginRequest = new Login();
        loginRequest.setEmail("smith@hospital.com");
        loginRequest.setPassword("secure123");

        validTokenResponse = new HashMap<>();
        validTokenResponse.put("valid", "true");

        invalidTokenResponse = new HashMap<>();
        invalidTokenResponse.put("error", "Invalid or expired token");
    }

    /**
     * Tests doctor availability endpoint with valid authorization.
     * 
     * Verifies availability check returns time slots when user is
     * authorized and doctor exists in system.
     */
    @Test
    void getDoctorAvailability_ValidToken_ReturnsAvailability() throws Exception {
        when(service.validateToken("valid-token", "patient"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(doctorService.getDoctorAvailability(1L, LocalDate.parse("2024-01-15")))
            .thenReturn(Arrays.asList("09:00-10:00", "14:00-15:00"));

        mockMvc.perform(get("/doctor/availability/patient/1/2024-01-15/" +
                            "valid-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").isArray())
            .andExpect(jsonPath("$.message[0]").value("09:00-10:00"));
    }

    /**
     * Tests availability check with invalid token.
     * 
     * Verifies unauthorized access is properly rejected with 401 status
     * when token validation fails.
     */
    @Test
    void getDoctorAvailability_InvalidToken_ReturnsUnauthorized() throws Exception {
        when(service.validateToken("invalid-token", "patient"))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new HashMap<>()));

        mockMvc.perform(get("/doctor/availability/patient/1/2024-01-15/" +
                            "invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    /**
     * Tests public endpoint for retrieving all doctors.
     * 
     * Verifies unauthenticated access to doctor list is allowed and
     * returns array of doctor objects.
     */
    @Test
    void getDoctor_NoAuth_ReturnsAllDoctors() throws Exception {
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(doctorService.getDoctors()).thenReturn(doctors);

        mockMvc.perform(get("/doctor"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.doctors").isArray())
            .andExpect(jsonPath("$.doctors[0].name").value("Dr. Smith"));
    }

    /**
     * Tests doctor creation by admin with valid token.
     * 
     * Verifies successful doctor addition returns 201 Created with
     * appropriate success message.
     */
    @Test
    void saveDoctor_ValidAdminToken_ReturnsCreated() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(doctorService.saveDoctor(any(Doctor.class)))
            .thenReturn(1);

        mockMvc.perform(post("/doctor/admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDoctor)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Doctor added to db"));
    }

    /**
     * Tests doctor creation when email already exists.
     * 
     * Verifies duplicate email detection returns 409 Conflict status
     * with appropriate error message.
     */
    @Test
    void saveDoctor_DuplicateEmail_ReturnsConflict() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(doctorService.saveDoctor(any(Doctor.class)))
            .thenReturn(-1);

        mockMvc.perform(post("/doctor/admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testDoctor)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Doctor already exists"));
    }

    /**
     * Tests doctor login with valid credentials.
     * 
     * Verifies successful authentication returns JWT token with
     * 200 OK status.
     */
    @Test
    void doctorLogin_ValidCredentials_ReturnsToken() throws Exception {
        Map<String, String> tokenResponse = Map.of("token", "doctor-jwt-token");
        when(doctorService.validateDoctor(any(Login.class)))
            .thenReturn(ResponseEntity.ok(tokenResponse));

        mockMvc.perform(post("/doctor/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("doctor-jwt-token"));
    }

    /**
     * Tests doctor login with invalid credentials.
     * 
     * Verifies failed authentication returns 401 Unauthorized with
     * error message.
     */
    @Test
    void doctorLogin_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        Map<String, String> errorResponse = Map.of(
            "error", 
            "Password does not match"
        );
        when(doctorService.validateDoctor(any(Login.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse));

        mockMvc.perform(post("/doctor/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Password does not match"));
    }

    /**
     * Tests doctor update with valid admin authorization.
     * 
     * Verifies successful update returns 200 OK with confirmation
     * message when admin token is valid.
     */
    @Test
    void updateDoctor_ValidAdminToken_ReturnsOk() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(doctorService.updateDoctor(any(DoctorUpdateDTO.class)))
            .thenReturn(1);

        mockMvc.perform(patch("/doctor/admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Doctor updated"));
    }

    /**
     * Tests doctor update for non-existent doctor.
     * 
     * Verifies update attempt on missing doctor returns 404 Not Found
     * with appropriate error message.
     */
    @Test
    void updateDoctor_NotFound_ReturnsNotFound() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(doctorService.updateDoctor(any(DoctorUpdateDTO.class)))
            .thenReturn(-1);

        mockMvc.perform(patch("/doctor/admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Doctor not found"));
    }

    /**
     * Tests doctor deletion with valid admin authorization.
     * 
     * Verifies successful deletion returns appropriate status with
     * confirmation message including deleted doctor ID.
     */
    @Test
    void deleteDoctor_ValidAdminToken_ReturnsSuccess() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(doctorService.deleteDoctor(1L))
            .thenReturn(1);

        mockMvc.perform(delete("/doctor/1/admin-token"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message")
                .value("Doctor deleted successfull with id: 1"));
    }

    /**
     * Tests deletion of non-existent doctor.
     * 
     * Verifies deletion attempt on missing doctor returns 404 Not Found
     * with appropriate error message.
     */
    @Test
    void deleteDoctor_NotFound_ReturnsNotFound() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(doctorService.deleteDoctor(999L))
            .thenReturn(-1);

        mockMvc.perform(delete("/doctor/999/admin-token"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message")
                .value("Doctor not found with id: 999"));
    }

    /**
     * Tests doctor filtering with all criteria.
     * 
     * Verifies filtering by name, time, and specialty returns filtered
     * results from service layer.
     */
    @Test
    void filter_AllCriteria_ReturnsFilteredDoctors() throws Exception {
        Map<String, Object> filterResponse = new HashMap<>();
        filterResponse.put("doctors", Arrays.asList(testDoctor));
        
        when(service.filterDoctor("Smith", "Cardiology", "am"))
            .thenReturn(filterResponse);

        mockMvc.perform(get("/doctor/filter/Smith/am/Cardiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.doctors").isArray())
            .andExpect(jsonPath("$.doctors[0].name").value("Dr. Smith"));
    }

    /**
     * Tests doctor filtering with null values.
     * 
     * Verifies that "null" string parameters are handled correctly
     * and return all doctors when no filters are applied.
     */
    @Test
    void filter_NullFilters_ReturnsAllDoctors() throws Exception {
        Map<String, Object> filterResponse = new HashMap<>();
        filterResponse.put("doctors", Arrays.asList(testDoctor));
        
        when(service.filterDoctor("null", "null", "null"))
            .thenReturn(filterResponse);

        mockMvc.perform(get("/doctor/filter/null/null/null"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.doctors").isArray());
    }

    /**
     * Tests doctor validation with missing required fields.
     * 
     * Verifies validation annotations trigger 400 Bad Request when
     * required fields are missing from doctor creation request.
     */
    @Test
    void saveDoctor_InvalidData_ReturnsBadRequest() throws Exception {
        Doctor invalidDoctor = new Doctor();
        
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));

        mockMvc.perform(post("/doctor/admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDoctor)))
            .andExpect(status().isBadRequest());
    }
}
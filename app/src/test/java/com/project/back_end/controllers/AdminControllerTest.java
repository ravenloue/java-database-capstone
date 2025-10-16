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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.back_end.models.Admin;

/**
 * Unit tests for AdminController endpoints.
 * 
 * Tests admin authentication and dashboard access functionality. Validates
 * token-based authentication and proper response handling. Uses MockMvc
 * for isolated controller testing without full Spring context.
 */
@WebMvcTest(controllers = AdminController.class)
@TestPropertySource(properties = {
    "api.path=/",
    "spring.jpa.hibernate.ddl-auto=none"
})
class AdminControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Admin testAdmin;
    private Map<String, String> successResponse;
    private Map<String, String> errorResponse;

    /**
     * Sets up test data before each test execution.
     * 
     * Initializes test admin object and response maps for consistent
     * test data across all test methods.
     */
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        testAdmin = new Admin();
        testAdmin.setUsername("admin");
        testAdmin.setPassword("password123");

        successResponse = new HashMap<>();
        successResponse.put("token", "valid-jwt-token");

        errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid credentials");
    }

    /**
     * Tests successful admin login with valid credentials.
     * 
     * Verifies that valid admin credentials return a JWT token with
     * 200 OK status. Ensures token is present in response body.
     */
    @Test
    void adminLogin_ValidCredentials_ReturnsToken() throws Exception {
        when(service.validateAdmin(any(Admin.class)))
            .thenReturn(ResponseEntity.ok(successResponse));

        mockMvc.perform(post("/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAdmin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("valid-jwt-token"));
    }

    /**
     * Tests admin login with invalid credentials.
     * 
     * Verifies that invalid credentials return 401 Unauthorized status
     * with appropriate error message in response body.
     */
    @Test
    void adminLogin_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        when(service.validateAdmin(any(Admin.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse));

        mockMvc.perform(post("/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAdmin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    /**
     * Tests admin dashboard access with valid token.
     * 
     * Based on the controller logic: returns dashboard when map is null.
     */
    @Test
    void adminDashboard_ValidToken_ReturnsView() throws Exception {
        when(service.validateToken("valid-token", "admin"))
            .thenReturn(ResponseEntity.ok(null));

        mockMvc.perform(get("/admin/dashboard/valid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("admin/adminDashboard"));
    }

    /**
     * Tests admin dashboard access that results in redirect.
     * 
     * When validateToken returns a non-null map, controller redirects.
     */
    @Test
    void adminDashboard_WithNonNullResponse_ReturnsRedirect() throws Exception {
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("any", "value");
        
        when(service.validateToken("redirect-token", "admin"))
            .thenReturn(ResponseEntity.ok(responseMap));

        mockMvc.perform(get("/admin/dashboard/redirect-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("redirect:/"));
    }

    /**
     * Tests admin dashboard with empty map response.
     * 
     * Even an empty map (not null) causes redirect per controller logic.
     */
    @Test
    void adminDashboard_EmptyResponse_ReturnsRedirect() throws Exception {
        when(service.validateToken("empty-token", "admin"))
            .thenReturn(ResponseEntity.ok(new HashMap<>()));

        mockMvc.perform(get("/admin/dashboard/empty-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("redirect:/"));
    }
}
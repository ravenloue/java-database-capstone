package com.project.back_end.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for ReportController REST endpoints.
 * 
 * Tests admin-only report generation endpoints including daily reports
 * and top doctor analytics. Validates proper authorization checks
 * for sensitive reporting data.
 */
@WebMvcTest(ReportController.class)
@TestPropertySource(properties = {
    "api.path=/",
    "spring.jpa.hibernate.ddl-auto=none"
})
class ReportControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Map<String, String> validTokenResponse;
    private Map<String, String> invalidTokenResponse;
    private Map<String, Object> reportData;

    /**
     * Sets up test fixtures before each test execution.
     * 
     * Initializes token responses and sample report data for
     * consistent testing across all test methods.
     */
    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        validTokenResponse = new HashMap<>();
        validTokenResponse.put("valid", "true");

        invalidTokenResponse = new HashMap<>();
        invalidTokenResponse.put("error", "Invalid or expired token");

        reportData = new HashMap<>();
        reportData.put("rows", Arrays.asList(
            Map.of("doctorName", "Dr. Smith", "appointments", 10),
            Map.of("doctorName", "Dr. Jones", "appointments", 8)
        ));
    }

    /**
     * Tests daily report generation with valid admin token.
     * 
     * Verifies daily appointment report returns data when admin
     * is properly authenticated.
     */
    @Test
    void daily_ValidAdminToken_ReturnsReport() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(reportService.daily(any(LocalDate.class)))
            .thenReturn(reportData);

        mockMvc.perform(get("/reports/daily/2024-01-15/admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rows").isArray())
            .andExpect(jsonPath("$.rows[0].doctorName").value("Dr. Smith"));
    }

    /**
     * Tests daily report with invalid admin token.
     * 
     * Verifies unauthorized access returns 401 status with error
     * message when token is invalid.
     */
    @Test
    void daily_InvalidToken_ReturnsUnauthorized() throws Exception {
        when(service.validateToken("invalid-token", "admin"))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(invalidTokenResponse));

        mockMvc.perform(get("/reports/daily/2024-01-15/invalid-token"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error")
                .value("Invalid or expired token"));
    }

    /**
     * Tests top doctors by month report with valid authorization.
     * 
     * Verifies monthly analytics return doctor performance data
     * for authorized admin users.
     */
    @Test
    void topByMonth_ValidAdminToken_ReturnsReport() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(reportService.topByMonth(1, 2024))
            .thenReturn(reportData);

        mockMvc.perform(get("/reports/top-doctor/month/1/2024/admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rows").isArray())
            .andExpect(jsonPath("$.rows[0].appointments").value(10));
    }

    /**
     * Tests monthly report with non-admin token.
     * 
     * Verifies that non-admin users cannot access reports even with
     * valid tokens for other roles.
     */
    @Test
    void topByMonth_NonAdminToken_ReturnsUnauthorized() throws Exception {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Insufficient privileges");
        
        when(service.validateToken("doctor-token", "admin"))
            .thenReturn(ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(errorResponse));

        mockMvc.perform(get("/reports/top-doctor/month/1/2024/doctor-token"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.error").value("Insufficient privileges"));
    }

    /**
     * Tests top doctors by year report with valid authorization.
     * 
     * Verifies yearly analytics return aggregated doctor performance
     * data for the specified year.
     */
    @Test
    void topByYear_ValidAdminToken_ReturnsReport() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(reportService.topByYear(2024))
            .thenReturn(reportData);

        mockMvc.perform(get("/reports/top-doctor/year/2024/admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rows").isArray())
            .andExpect(jsonPath("$.rows[1].doctorName").value("Dr. Jones"));
    }

    /**
     * Tests yearly report with expired token.
     * 
     * Verifies expired tokens are properly rejected with 401 status
     * even for previously valid admin users.
     */
    @Test
    void topByYear_ExpiredToken_ReturnsUnauthorized() throws Exception {
        Map<String, String> expiredResponse = new HashMap<>();
        expiredResponse.put("error", "Token has expired");
        
        when(service.validateToken("expired-token", "admin"))
            .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(expiredResponse));

        mockMvc.perform(get("/reports/top-doctor/year/2024/expired-token"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Token has expired"));
    }

    /**
     * Tests daily report with invalid date format.
     * 
     * Verifies that malformed date parameters result in 400 Bad Request
     * with appropriate error message.
     */
    @Test
    void daily_InvalidDateFormat_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/reports/daily/invalid-date/admin-token"))
            .andExpect(status().isBadRequest());
    }

    /**
     * Tests monthly report with invalid month value.
     * 
     * Verifies that month values outside 1-12 range are handled
     * appropriately by the service layer.
     */
    @Test
    void topByMonth_InvalidMonth_HandledByService() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(reportService.topByMonth(13, 2024))
            .thenReturn(Map.of("rows", Arrays.asList()));

        mockMvc.perform(get("/reports/top-doctor/month/13/2024/admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rows").isEmpty());
    }

    /**
     * Tests report generation when service returns empty data.
     * 
     * Verifies proper handling when no data is available for the
     * requested report period.
     */
    @Test
    void daily_NoData_ReturnsEmptyRows() throws Exception {
        when(service.validateToken("admin-token", "admin"))
            .thenReturn(ResponseEntity.ok(validTokenResponse));
        when(reportService.daily(any(LocalDate.class)))
            .thenReturn(Map.of("rows", Arrays.asList()));

        mockMvc.perform(get("/reports/daily/2024-01-15/admin-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rows").isArray())
            .andExpect(jsonPath("$.rows").isEmpty());
    }
}
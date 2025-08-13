package com.project.back_end.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for appointment management endpoints.
 * 
 * Provides HTTP endpoints for appointment CRUD operations. Validates
 * authentication tokens and delegates business logic to service layer.
 * Returns standardized JSON responses with appropriate HTTP status codes.
 */
@RestController
@RequestMapping("${api.path}"+"appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    /**
     * GET endpoint to retrieve appointments for a specific date.
     * 
     * Validates doctor token before processing. Filters appointments by date
     * and optionally by patient name. Returns list of appointments or error
     * message if token validation fails.
     * 
     * @param date ISO format date (YYYY-MM-DD) to filter appointments
     * @param patientName Patient name for filtering or "null" for all
     * @param token JWT token for doctor authentication
     * @return ResponseEntity with appointments list or error message
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map <String,Object>> getAppointments(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String patientName,@PathVariable String token) {
        Map<String, Object> map = new HashMap<>();
        ResponseEntity<Map<String,String>> tempMap = service.validateToken(token, "doctor");
        Map<String,String> tempBody = tempMap.getBody();
        if (tempMap.getStatusCode() != HttpStatus.OK ||
                (tempBody != null && tempBody.containsKey("error"))) {
            if (tempBody != null) map.putAll(tempBody);
            return new ResponseEntity<>(map, tempMap.getStatusCode());
        }

        map = appointmentService.getAppointment(patientName, date, token);
        return ResponseEntity.ok(map);
    }
    
    /**
     * POST endpoint to book a new appointment.
     * 
     * Validates patient token and appointment data. Checks doctor availability
     * and time conflicts before booking. Returns 201 Created on success or
     * appropriate error status with descriptive message.
     * 
     * @param appointment Appointment details in request body
     * @param token JWT token for patient authentication
     * @return ResponseEntity with booking status and message
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@RequestBody @Valid Appointment appointment,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> tempMap = service.validateToken(token, "patient");
        if (!tempMap.getBody().isEmpty()) {
            return tempMap;
        }

        Map<String, String> response = new HashMap<>();
        int out = service.validateAppointment(appointment);
        if (out == 1) {
            int res = appointmentService.bookAppointment(appointment);
            if (res == 1) {
                response.put("message", "Appointment Booked Successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 Created
            }
            response.put("message", "Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 409 Conflict
        } else if (out == -1) {
            response.put("message", "Invalid doctor id");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("message", "Appointment already booked for given time or Doctor not available");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * PUT endpoint to update existing appointment.
     * 
     * Validates patient token matches appointment owner. Allows rescheduling
     * or updating appointment details. Checks new time slot availability.
     * 
     * @param token JWT token for patient authentication
     * @param appointment Updated appointment data with ID
     * @return ResponseEntity with update status and message
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable String token, @RequestBody @Valid Appointment appointment) {
        ResponseEntity<Map<String, String>> tempMap = service.validateToken(token, "patient");
        if (!tempMap.getBody().isEmpty()) return tempMap;

        return appointmentService.updateAppointment(appointment);   
    }

    /**
     * DELETE endpoint to cancel an appointment.
     * 
     * Validates patient token and verifies appointment ownership. Removes
     * appointment from database if validation passes. Returns appropriate
     * status based on operation result.
     * 
     * @param id Appointment ID to cancel
     * @param token JWT token for patient authentication
     * @return ResponseEntity with cancellation status and message
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>>  cancelAppointment(@PathVariable Long id, @PathVariable String token) {
        ResponseEntity<Map<String, String>> tempMap = service.validateToken(token, "patient");
        if (!tempMap.getBody().isEmpty()) return tempMap;

        return appointmentService.cancelAppointment(id,token);
    }
}
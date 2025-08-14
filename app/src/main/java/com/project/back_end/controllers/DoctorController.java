package com.project.back_end.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


/**
 * REST controller for doctor-related operations.
 * 
 * Handles doctor CRUD operations, availability checks, and authentication.
 * Uses role-based access control with token validation. All endpoints
 * return standardized JSON responses with appropriate HTTP status codes.
 */
@RestController
@RequestMapping("${api.path}"+"doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService,Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    /**
     * Gets doctor availability for a specific date.
     * 
     * Validates user token before processing. Returns available time slots
     * for the specified doctor on the given date. User type must match the
     * token role for authorization.
     * 
     * @param user User type (patient/doctor/admin) for role validation
     * @param doctorId ID of doctor to check availability
     * @param date ISO format date (YYYY-MM-DD) to check
     * @param token JWT token for authentication
     * @return ResponseEntity with availability data or error message
     */
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String,Object>> getDoctorAvailability(
            @PathVariable String user, @PathVariable Long doctorId, 
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token) {
        Map<String, Object> map = new HashMap<>();
        ResponseEntity<Map<String,String>> tempMap= service.validateToken(token, user);
        if (!tempMap.getBody().isEmpty()) {
            map.putAll(tempMap.getBody());
            return new ResponseEntity<>(map, tempMap.getStatusCode());
        }

        map.put("message",doctorService.getDoctorAvailability(doctorId,date));
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /**
     * Retrieves all doctors without authentication.
     * 
     * Public endpoint that returns list of all doctors in the system.
     * Used for displaying doctors on public pages. No filtering applied.
     * 
     * @return ResponseEntity with doctors array in response body
     */
    @GetMapping
    public ResponseEntity<Map<String,Object>> getDoctor() {
        Map<String, Object> map=new HashMap<>();

        map.put("doctors",doctorService.getDoctors());
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    /**
     * Creates a new doctor account (Admin only).
     * 
     * Validates admin token before creating doctor. Checks for duplicate
     * email to prevent conflicts. Returns appropriate status based on
     * operation result.
     * 
     * @param doctor Doctor object with all required fields
     * @param token Admin JWT token for authorization
     * @return ResponseEntity with creation status and message
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(@RequestBody @Valid Doctor doctor,
            @PathVariable String token) {
        Map<String, String> response = new HashMap<>();
        ResponseEntity<Map<String,String>> tempMap= service.validateToken(token, "admin");
        if (tempMap.getBody().isEmpty()) return tempMap;

        int res = doctorService.saveDoctor(doctor);
        if (res == 1) {
            response.put("message", "Doctor added to db");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if(res == -1) {
            response.put("message", "Doctor already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    
        response.put("message", "Some internal error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);     
    }

    /**
     * Authenticates doctor login credentials.
     * 
     * Validates email and password combination. Returns JWT token on
     * successful authentication for subsequent API calls. No prior
     * authentication required.
     * 
     * @param login Login object containing email and password
     * @return ResponseEntity with JWT token or error message
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody @Valid Login login) {
        return doctorService.validateDoctor(login);
    }

    /**
     * Updates existing doctor information (Admin only).
     * 
     * Modifies doctor details in database. Requires valid admin token.
     * Doctor must exist in system for update to succeed.
     * 
     * @param doctor Updated doctor object with ID
     * @param token Admin JWT token for authorization
     * @return ResponseEntity with update status and message
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody @Valid Doctor doctor,
            @PathVariable String token) {
        Map<String, String> response = new HashMap<>();
        ResponseEntity<Map<String,String>> tempMap= service.validateToken(token, "admin");
        if (tempMap.getBody().isEmpty()) return tempMap;
    
        int res = doctorService.updateDoctor(doctor);
        if (res == 1) {
            response.put("message", "Doctor updated");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else if(res == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    
        response.put("message", "Some internal error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); 
    }

    /**
     * Deletes a doctor account (Admin only).
     * 
     * Removes doctor from system by ID. Validates admin token first.
     * Returns appropriate status based on whether doctor was found.
     * 
     * @param id Doctor ID to delete
     * @param token Admin JWT token for authorization
     * @return ResponseEntity with deletion status and message
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable long id,
            @PathVariable String token) {
        Map<String, String> response = new HashMap<>();
        ResponseEntity<Map<String,String>> tempMap= service.validateToken(token, "admin");
        if (tempMap.getBody().isEmpty()) return tempMap;

        int res = doctorService.deleteDoctor(id);
        if (res == 1) {
            response.put("message", "Doctor deleted successfull with id: "+id);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if(res ==-1) {
            response.put("message", "Doctor not found with id: "+id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        response.put("message", "Some internal error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Filters doctors by multiple criteria.
     * 
     * Public endpoint for searching doctors. Applies name (partial match),
     * available time, and specialty filters. Pass "null" string to skip
     * any filter criteria.
     * 
     * @param name Doctor name for partial match search
     * @param time Available time slot to filter by
     * @param speciality Medical specialty to filter by
     * @return ResponseEntity with filtered doctors array
     */
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name, @PathVariable String time, @PathVariable String speciality) {
        Map<String,Object> map=new HashMap<>();
        
        map=service.filterDoctor(name, speciality, time);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

}
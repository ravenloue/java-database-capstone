package com.project.back_end.controllers;

import com.project.back_end.services.Service;
import com.project.back_end.services.ReportService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.path}" + "reports")
public class ReportController {

    private final ReportService reportService;
    private final Service service;

    public ReportController(ReportService reportService, Service service) {
        this.reportService = reportService;
        this.service = service;
    }

    @GetMapping("/daily/{date}/{token}")
    public ResponseEntity<Map<String, Object>> daily(
            @PathVariable @DateTimeFormat(
                iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @PathVariable String token) {
        
        Map<String, Object> map = new HashMap<>();
        ResponseEntity<Map<String, String>> tempMap = service.validateToken(token, "admin");
        Map<String, String> tempBody = tempMap.getBody();
        if (tempMap.getStatusCode() != HttpStatus.OK ||
                (tempBody != null && tempBody.containsKey("error"))) {
            if (tempBody != null) map.putAll(tempBody);
            return new ResponseEntity<>(map, tempMap.getStatusCode());
        }

        map.putAll(reportService.daily(date));
        return ResponseEntity.ok(map);
    }

    @GetMapping("/top-doctor/month/{month}/{year}/{token}")
    public ResponseEntity<Map<String, Object>> topByMonth(
            @PathVariable int month,
            @PathVariable int year,
            @PathVariable String token) {

        Map<String, Object> map = new HashMap<>();
        ResponseEntity<Map<String, String>> tempMap = service.validateToken(token, "admin");
        Map<String, String> tempBody = tempMap.getBody();
        if (tempMap.getStatusCode() != HttpStatus.OK ||
                (tempBody != null && tempBody.containsKey("error"))) {
            if (tempBody != null) map.putAll(tempBody);
            return new ResponseEntity<>(map, tempMap.getStatusCode());
        }

        map.putAll(reportService.topByMonth(month, year));
        return ResponseEntity.ok(map);
    }

    @GetMapping("/top-doctor/year/{year}/{token}")
    public ResponseEntity<Map<String, Object>> topByYear(
            @PathVariable int year,
            @PathVariable String token) {

        Map<String, Object> map = new HashMap<>();
        ResponseEntity<Map<String, String>> tempMap = service.validateToken(token, "admin");
        Map<String, String> tempBody = tempMap.getBody();
        if (tempMap.getStatusCode() != HttpStatus.OK ||
                (tempBody != null && tempBody.containsKey("error"))) {
            if (tempBody != null) map.putAll(tempBody);
            return new ResponseEntity<>(map, tempMap.getStatusCode());
        }

        map.putAll(reportService.topByYear(year));
        return ResponseEntity.ok(map);
    }
}

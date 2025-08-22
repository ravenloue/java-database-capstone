// src/main/java/com/project/back_end/services/ReportService.java
package com.project.back_end.services;

import com.project.back_end.DTO.DailyApptRow;
import com.project.back_end.DTO.TopDoctorMonthRow;
import com.project.back_end.DTO.TopDoctorYearRow;
import com.project.back_end.repo.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final ReportRepository repo;

    public ReportService(ReportRepository repo) {
        this.repo = repo;
    }

    public Map<String, Object> daily(LocalDate date) {
        List<DailyApptRow> rows = repo.getDailyReport(date);
        return Map.of("rows", rows);
    }

    public Map<String, Object> topByMonth(int month, int year) {
        List<TopDoctorMonthRow> rows = repo.getTopDoctorByMonth(month, year);
        return Map.of("rows", rows);
    }

    public Map<String, Object> topByYear(int year) {
        List<TopDoctorYearRow> rows = repo.getTopDoctorByYear(year);
        return Map.of("rows", rows);
    }
}

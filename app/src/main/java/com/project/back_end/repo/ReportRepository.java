// src/main/java/com/project/back_end/repo/ReportRepository.java
package com.project.back_end.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.back_end.DTO.DailyApptRow;
import com.project.back_end.DTO.TopDoctorMonthRow;
import com.project.back_end.DTO.TopDoctorYearRow;
import com.project.back_end.models.Appointment;

public interface ReportRepository extends JpaRepository<Appointment, Long> {

    @Query(value = "SELECT * FROM get_daily_appointment_report_by_doctor(:date)", nativeQuery = true)
    List<DailyApptRow> getDailyReport(@Param("date") LocalDate date);

    @Query(value = "SELECT * FROM get_doctor_with_most_patients_by_month(:m, :y)", nativeQuery = true)
    List<TopDoctorMonthRow> getTopDoctorByMonth(@Param("m") int month, @Param("y") int year);

    @Query(value = "SELECT * FROM get_doctor_with_most_patients_by_year(:y)", nativeQuery = true)
    List<TopDoctorYearRow> getTopDoctorByYear(@Param("y") int year);
}

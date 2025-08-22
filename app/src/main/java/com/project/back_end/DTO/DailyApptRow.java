package com.project.back_end.DTO;

import java.time.LocalDateTime;

public interface DailyApptRow {
    String getDoctorName();
    LocalDateTime getAppointmentTime();
    Integer getStatus();
    String getPatientName();
    String getPatientPhone();
}

package com.project.back_end.models;

import java.beans.Transient;
import java.lang.annotation.Inherited;
import java.time.LocalDateTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "doctor name must not be null")
    private Doctor doctor;

    @ManyToOne
    @NotNull(message = "patient name must not be null")
    private Patient patient;

    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

    private int status; // 0 = Scheduled, 1 = Completed

    @Transient
    private LocalDateTime getEndTime() {
        return appointmentTime.plusHours(1);
    }

    @Transient
    private LocalDate getAppointmentDate() {
        return appointmentTime.toLocalDate();
    }

    @Transient
    private getAppointmentTimeOnly() {
        return appointmentTime.toLocalTime();
    }

    // No argument constructor
    public Appointment() {
        this.id = 0;
        this.doctor = "Doctor Name";
        this.patient = "John Doe";
        this.appointmentTime = null;
        this.status = 0;
    }

    public Appointment(Long id, Doctor doctor, Patient patient, LocalDateTime appointmentTime, int status) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public LocalDateTime getApptTime() {
        return appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public void setApptTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public void setStatus(int status) {
        this.status = status;
    }
    
}


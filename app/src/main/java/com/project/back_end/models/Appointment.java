package com.project.back_end.models;

import java.lang.Long;
import java.time.*;
import jakarta.persistence.Transient;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;


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

    private int status;

    @Transient
    public LocalDateTime getEndTime() {
        return appointmentTime.plusHours(1);
    }

    @Transient
    public LocalDate getAppointmentDate() {
        return appointmentTime.toLocalDate();
    }

    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return appointmentTime.toLocalTime();
    }

    // No argument constructor
    public Appointment() {
        this.id = 0L;
        this.doctor = null;
        this.patient = null;
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


package com.project.back_end.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Document(collection = "prescriptions")
public class Prescription {

    @Id
    private String id;

    @NotNull(message = "patientName must not be null")
    @Size(min = 3, max = 100)
    private String patientName;

    @NotNull(message = "appointmentId must not be null")
    private Long appointmentId;

    @NotNull(message = "medication name must not be null")
    @Size(min = 3, max = 100)
    private String medication;

    @NotNull(message = "dosage must not be null")
    private String dosage;

    @Size(max = 200)
    private String doctorNotes;

    // No argument constructor
    public Prescription() {
        this.id = "0";
        this.patientName = "Default Name";
        this.appointmentId = 0L;
        this.medication = "Medication";
        this.dosage = "Dosage";
        this.doctorNotes = "Notes";
    }

    public Prescription(String id, String patientName, Long appointmentId, String medication, String dosage, String doctorNotes) {
        this.id = id;
        this.patientName = patientName;
        this.appointmentId = appointmentId;
        this.medication = medication;
        this.dosage = dosage;
        this.doctorNotes = doctorNotes;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getPatientName() {
        return patientName;
    }

    public Long getApptId() {
        return appointmentId;
    }

    public String getMedication() {
        return medication;
    }

    public String getDosage() {
        return dosage;
    }

    public String getDoctorNotes() {
        return doctorNotes;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setApptId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setDoctorNotes(String doctorNotes) {
        this.doctorNotes = doctorNotes;
    }
}

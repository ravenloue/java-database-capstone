package com.project.back_end.DTO;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.persistence.ElementCollection;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DoctorUpdateDTO {
    
    @NotNull(message = "Doctor ID is required")
    private Long id;

    @NotBlank(message = "Doctor Name is required")
    private String name;

    @NotBlank(message = "Doctor Specialty is required")
    private String specialty;

    @NotBlank(message= "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$",
    		message = "The phone number must be in the following format ###-###-####")
    private String phone;

    @ElementCollection
    private List<String> availableTimes;

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @JsonProperty("availableTimes")
    public List<String> getAvailability() {
        return availableTimes;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("availableTimes")
    public void setAvailability(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }
}

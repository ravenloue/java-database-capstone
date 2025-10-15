package com.project.back_end.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name must not be null")
    @Size(min = 3, max = 100)
    private String name;

    @NotNull(message = "Specialty must not be null")
    @Size(min = 3, max = 50)
    private String specialty;
    
    @Email
    @NotNull(message = "Email must not be null")
    private String email;

    @Size(min = 6, max = 20,
    	  message = "The password must be between {min} and {max} characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

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

    public String getPassword() {
        return password;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @JsonProperty("availableTimes")
    public void setAvailability(List<String> availableTimes) {
        this.availableTimes = availableTimes;
    }

}


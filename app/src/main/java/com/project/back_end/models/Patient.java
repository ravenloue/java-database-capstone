package com.project.back_end.models;

import java.lang.Long;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

@Entity
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "name must not be null")
    @Size(min = 3, max = 100)
    private String name;

    @Email
    @NotNull(message = "email must not be null")
    private String email;

    @NotNull(message = "password must not be null")
    @Size(min = 6)
    private String password;

    @NotNull(message = "phone must not be null")
    @Pattern(regexp = "^\\d{3}-\\d{3}-\\d{4}$")
    private String phone;

    @NotNull(message = "address must not be null")
    @Size(max = 255)
    private String address;

    // No argument constructor
    public Patient() {
        this.id = 0L;
        this.name = "Default Name";
        this.email = "Email";
        this.password = "Password";
        this.phone = "Phone";
        this.address = "Address";
    }

    public Patient(Long id, String name, String email, String password, String phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public String getAddress() {
        return address;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setAddress(String address) {
        this.address = address;
    }
}

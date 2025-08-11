package com.project.back_end.DTO;

/**
 * DTO for receiving login credentials from the client.
 * Used for authentication purposes only.
 */
public class Login {

    private String identifier;
    private String password;

    // Default constructor
    public Login() {
    }

    // Getters
    public String getEmail() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    // Setters
    public void setEmail(String identifier) {
        this.identifier = identifier;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
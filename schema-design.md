# Smart Clinic System
#### Database Schema Design

This document outlines the relational and document-based schema designs for the
Smart Clinic application. The system uses a hybrid database architecture: 
MySQL for structured data with strong relationships and constraints, and 
MongoDB for flexible, evolving data like prescriptions.

---

## MySQL Schema Design

The relational database (MySQL) is used for managing core entities such as 
users, appointments, and roles. Below are four core tables with defined 
attributes, data types, keys, and constraints.

### Patients Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| patient_id | INT | PRIMARY KEY, AUTO_INCREMENT |
| full_name | VARCHAR(100) | NOT NULL |
|| email | VARCHAR(100) | NOT NULL, UNIQU  
|| date_of_birth | DATE | NOT NULL |
| gender | ENUM('M','F','Other') | NOT NULL |
| phone_number | VARCHAR(15) | |
| created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

### Doctors Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| doctor_id | INT | PRIMARY KEY |
| full_name | VARCHAR(100) | NOT NULL |
| specialization | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | NOT NULL |
| phone_number | VARCHAR(15) |          
| is_active | BOOLEAN | DEFAULT TRUE |

 ### Appointments Tble

| Column Name |Data Type | Constraints |
|-------------|----------|-------------|
| appointment | INT | PRIMARY KEY, AUTO_INCREMENT |
| patient_id | INT | FOREIGN KEY REFERENCES `patients`(patient_id) ON DELETE CASCADE |
| doctor_id | INT | FOREIGN KEY REFERENCES `doctors`(doctor_id) ON DELETE CASCADE |
| appointment_date | DATETIME | NOT NULL |
| status | ENUM('Scheduled', 'Completed', 'Cancelled') | DEFAULT 'Scheduled' |

### Admins Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| admin_id | INT | PRIMARY KEY, AUTO_INCREMENT |
| full_name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(100) | NOT NULL, UNIQUE |
| password_hash | VARCHAR(255) | NOT NULL |
| role | ENUM('SuperAdmin', 'Moderator') | DEFAULT 'Moderator' |

---

## MongoDB Collection Design
MongoDB is used for storing flexible, nested documents that may evolve over 
time. The prescriptions collection stores data for medications prescribed by 
doctors to patients.

### Collection: Prescriptions

Each document includes nested fields and an array of prescribed medications.

```json
{
  "_id": "64ecf123ab456c789def0123",
  "patient_id": 102,
  "doctor_id": 55,
  "prescribed_at": "2025-08-01T14:30:00Z",
  "notes": "Patient reports mild side effects; dose reduced.",
  "medications": [
    {
      "name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "3 times a day",
      "duration_days": 7
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "frequency": "as needed",
      "duration_days": 3
    }
  ],
  "follow_up_required": true
}
# Smart Clinic Database Schema Design

This document outlines the relational schema design for the Smart Clinic
application. The system now uses a single database architecture: PostgreSQL.
All application data&mdash;including structured entities with strong relationships
(patients, doctors, appointments) as well as previously document-based records
like prescriptions&mdash;are now modeled and stored in PostgreSQL. This change
simplifies deployment, hosting, and maintenance by consolidating both
relational and flexible data under one robust relational database system.

---

### Admins Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| id | bigint | PRIMARY KEY |
| username | text | NOT NULL |
| password | text | NOT NULL |

### Appointments Tble

| Column Name |Data Type | Constraints |
|-------------|----------|-------------|
| id | bigint | PRIMARY KEY |
| patient_id | bigint | FOREIGN KEY REFERENCES `patients`(id) |
| doctor_id | bigint | FOREIGN KEY REFERENCES `doctors`(id) |
| appointment_time | DATETIME | NOT NULL |
| status | integer | NOT NULL |

### Doctors Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| id | bigint | PRIMARY KEY |
| email | text | NOT NULL |
| name | text | NOT NULL |
| password | text | NOT NULL |
| specialty | text | NOT NULL |
| phone | text | NOT NULL |  

### Doctor Available Times Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| doctor_id | bigint | FOREIGN KEY REFERENCES `doctors`(id) |
| available_times | text | NOT NULL |

### Patients Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| id | bigint | PRIMARY KEY, AUTO_INCREMENT |
| name | text | NOT NULL |
| email | text | NOT NULL, UNIQUE |
| phone | text | NOT NULL |
| address | text | NOT NULL |
| password | text | NOT NULL |

### Prescriptions Table

| Column Name | Data Type | Constraints |
|-------------|-----------|-------------|
| id | text | PRIMARY KEY |
| patient_name | text | NOT NULL |
| appointment_id | bigint | NOT NULL |
| medication | text | NOT NULL |
| dosage | text | NOT NULL |
| doctor_notes | text | NOT NULL |

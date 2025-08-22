# Smart Clinic Management System

A healthcare management application built with **Spring Boot** and **PostgreSQL**. It provides role-based dashboards for administrators, doctors, and patients, enabling appointment scheduling, prescription management, and reporting features.

## Table of Contents

* [Overview](#overview)
  * [The Idea](#the-idea)
  * [Screenshots](#screenshots)
* [My Process](#my-process)
  * [Built With](#built-with)
  * [What I learned](#what-i-learned)
  * [Continued development](#continued-development)
  * [Useful resources](#useful-resources)
* [Author](#author)
* [Acknowledgments](#acknowledgments)

---

## Overview

The Smart Clinic Management System project was developed as a full-stack healthcare system with a layered architecture during the [IBM Java Developer Professional Course on Coursera](https://www.coursera.org/professional-certificates/java-developer). It manages patient-doctor interactions, supports appointment booking, and generates admin reports using PostgreSQL functions. Unlike the original capstone project that used a hybrid of MySQL and MongoDB, this system was redesigned for PostgreSQL only, simplifying the data model and allowed for me to only have to worry about hosting a single database. 

### The Idea

* Create a role-based healthcare platform:
  * Patients book and manage appointments.
  * Doctors view appointments and can manage prescription information.
  * Admins manage doctors and can pull reports.
* Implement strong relational integrity and reporting through PostgreSQL.
* Provide both RESTful APIs and server-rendered dashboards.

### Screenshots

![landing page](https://github.com/ravenloue/java-database-capstone/blob/main/ss/landing-page.png)
[admin dashboard](https://github.com/ravenloue/java-database-capstone/blob/main/ss/admin-logged-in.png) | [doctor dashboard](https://github.com/ravenloue/java-database-capstone/blob/main/ss/doctor-logged-in.png) | [patient dashboard](https://github.com/ravenloue/java-database-capstone/blob/main/ss/patient-dash.png)

---

## My Process

### Built with

* Java 17
* Spring Boot
* PostgreSQL
* Spring Data JPA
* Thymeleaf + Vanilla JS
* HTML5 + CSS3
* Docker
* Render

### What I learned

* Managing role-based access in a full-stack app with JWT authentication.
* Building layered architecture: Controllers → Services → Repositories → Database.
* How to migrate a hybrid MySQL/MongoDB system into a pure PostgreSQL schema.
* Writing and integrating PostgreSQL functions with Spring Data JPA for admin reports.

### Continued development

* **August 2025**
  * **August 22** - Added screenshots after getting it hosted live.
  * **August 21** – Added upcoming appointments feature for doctors.
  * **August 20** - Migrated prescriptions from MongoDB to PostgreSQL tables.
  * **August 18** - Migrated from MySQL to normalized PostgreSQL for deployment purposes.
* **July 2025** – Completed IBM Java Developer Capstone project and received certification.
* **Earlier** – Completed patient dashboard with appointment booking modal, integrated JWT login.

### Useful resources

* [Spring Boot Documentation](https://spring.io/projects/spring-boot) – For backend structure.
* [PostgreSQL Functions Tutorial](https://www.postgresql.org/docs/current/sql-createfunction.html) – Helped with admin reports.
* [Thymeleaf Docs](https://www.thymeleaf.org/documentation.html) – Used for dashboards.

---

## Author

* GitHub – [ravenloue](https://github.com/ravenloue)
* LinkedIn – [Brandie Mallard](https://www.linkedin.com/in/brandie-mallard-0554aa219/)
* Twitter – [@ravenloue](https://www.twitter.com/ravenloue)

---

## Acknowledgments

* Thanks to various tutorials on PostgreSQL functions and Spring Data JPA.
* Thanks to the IBM Java Development Capstone project for providing the project idea as a starting place.

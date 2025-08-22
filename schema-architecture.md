# Smart Clinic Schema Architecture

The Smart Clinic app is designed with a layered architecture that cleanly
separates concerns across presentation, business logic, and data access. Built
with Spring Boot, the system adheres to best practices for modularity and
maintainability. The architecture supports both server-rendered web views and
RESTful APIs, enabling flexible interactions for different user roles such as
administrators, doctors, and patients.

The User Interface Layer accommodates both Thymeleaf-based dashboards—like the
AdminDashboard and DoctorDashboard—and REST API endpoints consumed by client
applications such as the PatientDashboard. This dual-mode approach ensures
support for traditional web workflows as well as modern integrations with
mobile apps or single-page applications via JSON.

Requests initiated by users are routed through the Controller Layer, where
Thymeleaf controllers handle HTML rendering and REST controllers manage API
requests. Controllers validate input, enforce security (via tokens), and
forward requests to the Service Layer. The services encapsulate business logic,
such as ensuring a doctor is available before booking an appointment, managing
patient histories, or validating prescription rules.

At the Repository Layer, Spring Data JPA is leveraged to abstract persistence
operations. All data now resides in a unified PostgreSQL database, replacing
the previous dual-database design. PostgreSQL manages both relational entities
(patients, doctors, appointments, schedules) and what were previously
“document-style” records such as prescriptions. By normalizing prescriptions
and related entities into relational tables, PostgreSQL ensures consistent
schema enforcement, transactional safety, and easier querying across all
clinical data.

Data retrieved from the database is automatically mapped into Java domain
models using JPA’s @Entity annotations. These models flow back through the
layers, either injected into Thymeleaf templates for server-side rendering or
serialized into JSON for REST API responses. This consistent flow completes a
robust, scalable request-response cycle within the application.

## End-to-End Request Lifecycle

1. User Request Initiation (UI Layer)
   - Users interact with the system via dashboards (Thymeleaf-based) or REST clients.
   - Requests are sent to the backend to either render a page or access an API endpoint.
2. Request Routing (Controller Layer)
   - Controllers handle incoming requests based on URL path and HTTP method.
   - Thymeleaf controllers prepare HTML views, while REST controllers format JSON responses.
   - Input validation and token authentication are handled here before passing to services.
3. Business Logic Processing (Service Layer)
   - Core business rules are executed in the service layer.
   - Examples include appointment booking workflows, prescription creation, and patient-doctor matching.
4. Data Access (Repository Layer)
   - The service layer calls repository interfaces backed by Spring Data JPA.
     - All queries are executed against PostgreSQL, which now holds all application data in relational tables.
5. Database Interaction (PostgreSQL)
   - PostgreSQL handles structured, normalized data for every domain entity: users, roles, doctors, patients, appointments, and prescriptions.
     - What was once managed by MongoDB (e.g., prescriptions) is now modeled as relational tables with foreign key relationships.
6. Model Binding (Mapping to Java Objects)
   - Query results are mapped into @Entity classes.
     - The unified relational model eliminates the need for @Document classes, simplifying persistence and ensuring stronger data consistency.
7. Response Delivery (Returning to UI)
   - Data flows back up to controllers.
   - Thymeleaf controllers inject models into HTML templates.
   - REST controllers return JSON responses for client apps.

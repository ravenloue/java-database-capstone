The Smart Clinic app is designed with a layered architecture that cleanly 
separates concerns across presentation, business logic, and data access. Built 
using Spring Boot, the system adheres to best practices for modular development
and maintainability. The architecture supports both server-rendered web views 
and RESTful APIs, enabling flexible interaction for different user types such 
as administrators, doctors, and patients. The User Interface Layer accommodates
both Thymeleaf-based dashboards—like the AdminDashboard and DoctorDashboard—and
API endpoints consumed by client applications or modules like PatientDashboard. 
This dual-mode interface allows the system to support both traditional web 
interactions and mobile or SPA (Single Page Application) integrations via JSON.

Requests initated by users are processed through the Controller Layer, where 
Thymeleaf Controllers handle HTML rendering and REST Controllers handle API
communication. These controllers route requests to the Service Layer, which
encapsulates business logic, enforces validation, and manages multi-entity
workflows—ensuring, for instance, that a doctor is available before booking an
appointment. The Repository Layer then handles all data persistence operations,
abstracting interactions with either a MySQL database for structured data
(patients, appointments, etc.) or a MongoDB database for document-style data
(e.g., prescriptions). Data retrieved is mapped into Java model classes using
model binding, either as @Entity JPA classes for MySQL or @Document classes for
MongoDB. These models are then returned to the UI—either embedded in dynamic
HTML via Thymeleaf or serialized as JSON for REST responses—completing a robust,
scalable request-response cycle. 

---

1. User Request Initiation (UI Layer)
    - A user interacts with the system either through a Thymeleaf-based web
    dashboard or a REST client. The request is sent to the backend via an HTTP
    call—either to render a web page or acces an API endpoint
2. Request Routing (Controller Layer)
    - The request is received by a backend controller, determined by the URL path
    and HTTP method. Thymeleaf controllers handle requests for HTML views,
    while REST controllers handle API calls. The controller validates input and
    forwards the request to the appropriate service.
3. Business Logic Processing (Service Layer)
    - The controller delegates the request to the service layer, where core 
    business logic is applied. This layer handles tasks such as validating
    inputs, enforcing rules, and coordinating actions across multiple entities.
4. Data Access (Repository Layer)
    - The service invokes methods in the repository layer to perform database
    operations. Spring Data JPA is used to query MySQL for relational data,
    while Spring Data MongoDB handles document-based data.
5. Database Interaction (DB Engines)
    - The repositories communicate directly with the respective databases:
      - MySQL manages structured, normalized entities like users, roles and 
      schedules.
      - MongoDB handles flexible, schema-less data like prescriptions that 
      may vary in structure
6. Model Binding (Data mapping to Java Objects)
    - Retrieved data is automatically mapped into Java model classes. MySQL
    results are mapped to @Entity classes, while MongoDB documents are mapped
    to @Document classes, providing a consistent object-oriented representation
    of the data.
7. Response Delivery (Returning to UI)
    - The bound models are returned to the controller and passed to the
    appropriate response format:
      - Thymeleaf controllers inject the models into HTML templates for
      server-side rendering.
      - REST controllers serialize the models into JSON for API clients. The
      result is sent back to the user's browser or app, completing the
      request-response cycle.
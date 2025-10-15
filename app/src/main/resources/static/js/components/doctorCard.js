// doctorCard.js
import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";
import { openModal } from "./modals.js";

/**
 * Creates a doctor card component for display in the dashboard.
 * 
 * Generates a card element containing doctor information and action buttons
 * based on the current user's role. Admin users see update/delete buttons,
 * patients see booking buttons, and logged-in patients can directly book
 * appointments. Card structure includes doctor details (name, specialty,
 * email, availability) and role-specific action buttons.
 * 
 * @export
 * @param {Object} doctor - Doctor object containing all doctor information
 * @param {string} doctor.id - Unique identifier for the doctor
 * @param {string} doctor.name - Full name of the doctor
 * @param {string} doctor.specialty - Medical specialization
 * @param {string} doctor.email - Contact email address
 * @param {Array<string>} doctor.availableTimes - Array of available time slots
 * @returns {HTMLElement} Complete doctor card element ready for DOM insertion
 */
export function createDoctorCard(doctor) {
  const role = localStorage.getItem("userRole");

  /**
   * Doctor Information Section
   * 
   * Creates the main card container and populates it with doctor
   * details including name, specialization, email, and availability.
   * Uses Object.assign for concise property assignment.
   */
  const card = document.createElement("div");
  const infoDiv = Object.assign(document.createElement("div"), 
  	{
      id: `${doctor.id}`,
      className: 'doctor-info'
	});
  const name = Object.assign(document.createElement("h3"), 
  	{
      textContent: `${doctor.name}`
	});
  const spec = Object.assign(document.createElement("p"),
    {
      textContent: `Specialization: ${doctor.specialty}`
  });
  const email = Object.assign(document.createElement("p"),
    {
      textContent: `Email: ${doctor.email}`
  });
  const avail = Object.assign(document.createElement("p"),
    {
      textContent: `Available: ${doctor.availableTimes.join(", ")}`
  });
  
  card.classList.add("doctor-card");
  infoDiv.append(name, spec, email, avail);

  /**
   * Action Buttons Container
   * 
   * Creates container for role-specific action buttons. Button types
   * and handlers vary based on user role (admin, patient, loggedPatient).
   */
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  /**
   * Admin Action Buttons
   * 
   * Provides update and delete functionality for administrators.
   * Delete button includes confirmation dialog and token validation.
   * Update button opens modal with pre-filled doctor information.
   */
  if (role === "admin") { 
    const removeBtn = Object.assign(document.createElement("button"),
      {
        id: "deleteDocBtn",
        textContent: "Delete"
    });
	  const updateBtn = Object.assign(document.createElement("button"),
      {
        id: "updateDocBtn",
        textContent: "Update"
    });
	
    /**
     * Delete Button Handler
     * 
     * Confirms deletion with user, validates admin token, and calls
     * deleteDoctor service. Removes card from DOM on successful deletion.
     * Shows appropriate error messages for failed operations.
     */
    removeBtn.addEventListener("click", async () => {
      const confirmDelete = confirm(`Are you sure you want to delete ${doctor.name}?`);
      if (!confirmDelete) return;
  
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Admin token not found. Please log in again.");
        return;
      }
  
      const { success, message } = await deleteDoctor(doctor.id, token);
  
      if (success) {
        alert(message || "Doctor deleted successfully");
        card.remove();
      } else {
        alert(message || "Failed to delete doctor");
      }
    });
    
    /**
     * Update Button Handler
     * 
     * Opens modal in edit mode with current doctor data pre-filled.
     * Passes doctor object to modal for field population. Modal handles
     * form submission and calls appropriate update service.
     */
    updateBtn.addEventListener("click", () => {
      openModal('updateDoctor', doctor);
    });

	actionsDiv.appendChild(updateBtn);
	actionsDiv.appendChild(removeBtn);	
  }
  /**
   * Patient Action Button
   * 
   * Shows book now button for non-authenticated patients. Clicking
   * prompts user to log in before booking appointments.
   */
  else if(role === 'patient'){ 
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click", () => {
      alert("Patient need to login first.");
    });

    actionsDiv.appendChild(bookNow);
  }
  /**
   * Logged Patient Action Button
   * 
   * Provides direct booking functionality for authenticated patients.
   * Validates token, fetches patient data, and opens booking overlay
   * with doctor and patient information pre-populated.
   */
  else if(role === 'loggedPatient'){ 
    const bookNow = document.createElement("button");
    const token = localStorage.getItem("token");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click",async (e) => {
      if(!token){
        alert("Login is required for booking appointment");
        localStorage.setItem("userRole", "patient");
        window.location.href = "/pages/patientDashboard.html";
      }

      const patientData = await getPatientData(token);
      if (!patientData) {
        alert("Failed to fetch patient details.");
        return;
      }
      showBookingOverlay(e, doctor, patientData);
    });

    actionsDiv.appendChild(bookNow);
  }

  /**
   * Card Assembly
   * 
   * Combines information section and action buttons into complete
   * card element. Returns assembled card for dashboard rendering.
   */
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);
  return card;
}
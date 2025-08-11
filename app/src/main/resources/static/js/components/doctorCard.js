// doctorCard.js
import { showBookingOverlay } from "../loggedPatient.js";
import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

export function createDoctorCard(doctor) {
  const role = localStorage.getItem("userRole");

  // Doctor information container
  const card = document.createElement("div");
  const infoDiv = document.createElement("div");
  const name = document.createElement("h3");
  const spec = document.createElement("p");
  const email = document.createElement("p");
  const avail = document.createElement("p");

  // Button container
  const actionsDiv = document.createElement("div");

  // Doctor information
  card.classList.add("doctor-card");
  infoDiv.classList.add("doctor-info");
  name.textContent = `${doctor.name}`;
  spec.textContent = `Specialization: ${doctor.specialty}`;
  email.textContent = `Email: ${doctor.email}`;
  avail.textContent = `Available: ${doctor.availableTimes.join(", ")}`;
  infoDiv.append(name, spec, email, avail);

  // Populate the button container
  actionsDiv.classList.add("card-actions");
  if (role === "admin") { // Admin buttons
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
  
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
  
    actionsDiv.appendChild(removeBtn);
  } else if(role === 'patient'){ // Patient buttons
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";

    bookNow.addEventListener("click", () => {
      alert("Patient need to login first.");
    });

    actionsDiv.appendChild(bookNow);
  } else if(role === 'loggedPatient'){ // Logged-in Patient
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

  // Assemble the card
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);
  return card;
}
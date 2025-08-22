// doctorDashboard.js
import { getAllAppointments, getUpcomingAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const tableBody = document.getElementById("patientTableBody");
let selectedDate = null;
let token = localStorage.getItem("token");
let patientName = null;
let mode = "upcoming";

// Event Listeners Section

/** 
 * Search Bar Input Handler
 * 
 * Filters appointments by patient name. Clears filter when input is empty.
 * Triggers loadAppointments() on each input change for live filtering.
 */
document.getElementById("searchBar").addEventListener("input", (e) => {
  const value = e.target.value.trim();
  patientName = value.length > 0 ? value : null;
  loadAppointments();
});

/**
 * Today Button Click Handler
 * 
 * Resets the date picker to current date and reloads appointments. Updates
 * both the selectedDate variable and the date picker UI element.
 */
document.getElementById("todayButton").addEventListener("click", () => {
  mode = "byDate";
  selectedDate = new Date().toISOString().split('T')[0];
  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});

/**
 * Date Picker Change Handler
 * 
 * Updates selectedDate when user selects a new date from the picker.
 * Automatically refreshes the appointment list for the selected date.
 */
document.getElementById("datePicker").addEventListener("change", (e) => {
  const value = e.target.value?.trim();
  if (value) {
    mode = "byDate";
    selectedDate = value;
  } else {
    mode = "upcoming";
    selectedDate = null;
  }
  loadAppointments();
});

/**
 * Loads appointments from API and populates the table.
 * 
 * Fetches appointments using current filters (date and optional patient
 * name). Handles empty results and errors gracefully with user-friendly
 * messages. Creates table rows for each appointment with patient details.
 * 
 * API expects: date (YYYY-MM-DD), patientName (string or "null"), token
 * 
 * @async
 * @returns {Promise<void>} Completes when table is populated or error shown
 */
async function loadAppointments() {
  try {
    tableBody.innerHTML = `<tr><td colspan="5">Loading...</td></tr>`;

    let response;
    if (mode === "upcoming") {
      response = await getUpcomingAppointments(token, patientName);
    } else {
      // mode === 'byDate'
      response = await getAllAppointments(selectedDate, patientName, token);
    }

    const appointments = response.appointments || [];
    tableBody.innerHTML = "";

    if (appointments.length === 0) {
      const label =
        mode === "upcoming"
          ? "upcoming"
          : `for ${new Date(selectedDate).toLocaleDateString()}`;
      tableBody.innerHTML = `<tr><td colspan="5">No appointments found ${label}.</td></tr>`;
      return;
    }

    appointments.forEach((appointment) => {
      const patient = {
        id: appointment.patientId,
        name: appointment.patientName,
        phone: appointment.patientPhone,
        email: appointment.patientEmail,
      };
      const row = createPatientRow(patient, appointment.id, appointment.doctorId);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    tableBody.innerHTML = `<tr><td colspan="5">Error loading appointments. Try again later.</td></tr>`;
  }
}

/**
 * DOM Content Loaded Event Listener
 * 
 * Initializes the dashboard when page loads. Sets date picker to current date,
 * renders page content, and loads initial appointment list. Ensures all DOM
 * elements are available before attempting to access them.
 */
window.addEventListener("DOMContentLoaded", () => {
  renderContent?.();
  loadAppointments();
});
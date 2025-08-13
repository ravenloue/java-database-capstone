// adminDashboard.js
import { openModal } from './components/modals.js';
import { getDoctors  , filterDoctors , saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';
//import './util.js';


// Event Listeners
/** 
 * Add Doctor Button Click Handler
 * 
 * Opens the modal dialog for adding a new doctor to the system.
 */
document.getElementById('addDocBtn').addEventListener('click', () => {
  openModal('addDoctor');
});

/**
 * DOM Content Loaded Handler
 * 
 * Initializes the admin dashboard by loading all doctor cards when the
 * page is fully loaded and DOM elements are ready.
 */
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});

/**
 * Search Bar Input Handler
 * 
 * Implements debounced search with 300ms delay to prevent excessive API
 * calls while user types. Triggers doctor filtering on each input change.
 */
document.getElementById("searchBar").addEventListener(
    "input", debounce(filterDoctorsOnChange, 300));

/**
 * Time Filter Change Handler
 * 
 * Triggers doctor filtering when user selects a different time slot from
 * the dropdown. Works in combination with other active filters.
 */
document.getElementById("filterTime").addEventListener(
    "change", filterDoctorsOnChange);

/**
 * Specialty Filter Change Handler
 * 
 * Triggers doctor filtering when user selects a different medical
 * specialty. Combines with search and time filters for refined results.
 */
document.getElementById("filterSpecialty").addEventListener(
    "change", filterDoctorsOnChange);

/**
 * Loads all doctor cards from the API and renders them.
 * 
 * Fetches the complete doctor list on initial page load. Creates card
 * elements for each doctor and appends to content area. Logs errors
 * to console if the fetch operation fails.
 * 
 * @export
 * @function loadDoctorCards
 */
export function loadDoctorCards() {
  getDoctors()
    .then(doctors => {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = ""; 

      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    })
    .catch(error => {
      console.error(" Failed to load doctors:", error);
    });
}

/**
 * Filters doctors based on search and dropdown selections.
 * 
 * Collects values from search bar, time filter, and specialty filter.
 * Sends null for empty filters to get all results. Updates UI with
 * filtered results or shows "no doctors found" message.
 */
function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar").value.trim(); 
  const filterTime = document.getElementById("filterTime").value;  
  const filterSpecialty = document.getElementById("filterSpecialty").value;  
  const name = searchBar.length > 0 ? searchBar : null;  
  const time = filterTime.length > 0 ? filterTime : null;
  const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;

  filterDoctors(name , time ,specialty)
    .then(response => {
      const doctors = response.doctors;
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = ""; 

      if (doctors.length > 0) {
        console.log(doctors);
        doctors.forEach(doctor => {
          const card = createDoctorCard(doctor);
          contentDiv.appendChild(card);
        });
      } else {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
      }
    })
    .catch(error => {
      console.error("Failed to filter doctors:", error);
      alert("An error occurred while filtering doctors.");
    });
}

/**
 * Renders an array of doctors as cards in the UI.
 * 
 * Utility function that clears the content area and populates it with
 * doctor cards. Used by both initial load and filter operations to
 * maintain consistent rendering logic.
 * 
 * @export
 * @param {Array} doctors - Array of doctor objects to render
 */
export function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = ""; 

      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
}

/**
 * Handles the doctor addition form submission from modal.
 * 
 * Collects form data including name, specialty, credentials, and available
 * times. Validates token presence before submission. Shows success/error
 * alerts and refreshes page on successful addition. Global function for
 * modal access.
 * 
 * @async
 * @global
 * @function adminAddDoctor
 */
window.adminAddDoctor = async function() {
  const name = document.getElementById('doctorName').value;
        const specialty = document.getElementById('specialization').value;
        const email = document.getElementById('doctorEmail').value;
        const password = document.getElementById('doctorPassword').value;
        const phone = document.getElementById('doctorPhone').value;
        const checkboxes = document.querySelectorAll('input[name="availability"]:checked');
        const availableTimes = Array.from(checkboxes).map(cb => cb.value);
  
        const token = localStorage.getItem("token");
        if (!token) {
          alert("Token expired or not found. Please log in again.");
          return;
        }
  
        const doctor = {
          name,
          specialty,
          email,
          password,
          phone,
          availableTimes
        };
  
        const { success, message } = await saveDoctor(doctor, token);
  
        if (success) {
          alert(message);
          document.getElementById("modal").style.display = "none";
          window.location.reload();
        } else {
          alert("Error: " + message);
        }
}
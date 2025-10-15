// adminDashboard.js
import { API_BASE_URL } from './config/config.js';
import { openModal } from './components/modals.js';
import { getDoctors , filterDoctors , saveDoctor, updateDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

let doctors = getDoctors();

// Event Listeners
document.getElementById('addDocBtn').addEventListener('click', () => {
  openModal('addDoctor');
});
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});
document.getElementById("searchBar").addEventListener(
    "input", debounce(filterDoctorsOnChange, 300));
document.getElementById("filterTime").addEventListener(
    "change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener(
    "change", filterDoctorsOnChange);
document.getElementById('btnDailyReport').addEventListener(
    'click', runDailyReport);
document.getElementById('btnTopByMonth').addEventListener(
    'click', runTopByMonth);
document.getElementById('btnTopByYear').addEventListener(
    'click', runTopByYear);
document.getElementById('btnBackToDoctors').addEventListener(
    'click', loadDoctorCards(doctors));


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
export function loadDoctorCards(_doctors) {
    try { 
	  doctors => {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = ""; 

      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    }
	} catch(error){
      console.error(" Failed to load doctors:", error);
    }
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
        document.getElementById("modal").style.visibility = "hidden";
        window.location.reload();
    } else {
        alert("Error: " + message);
    }
}

/**
 * Handles the doctor update form submission from modal.
 * 
 * Collects updated form data and compares with existing values. Only sends
 * changed fields to backend to preserve unchanged data. Validates token
 * presence before submission. Shows success/error alerts and refreshes page
 * on successful update. Global function for modal access.
 * 
 * @async
 * @global
 * @function adminUpdateDoctor
 */
window.adminUpdateDoctor = async function() {
    const id = document.getElementById('doctorId').value;
    const name = document.getElementById('doctorName').value;
    const specialty = document.getElementById('specialization').value;
    const email = document.getElementById('doctorEmail').value;
    const phone = document.getElementById('doctorPhone').value;
    const checkboxes = document.querySelectorAll('input[name="availability"]:checked');
    const availableTimes = Array.from(checkboxes).map(cb => cb.value);

    // Validate required fields
    if (!name || !specialty || !email) {
        alert("Please fill in all required fields");
        return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Token expired or not found. Please log in again.");
        return;
    }

    // Build update object
    const updateData = {
        id,
        name,
        specialty,
        email,
        phone,
        availableTimes
    };
    
    const { success, message } = await updateDoctor(updateData, token);

    if (success) {
        alert(message || "Doctor updated successfully");
        document.getElementById("modal").style.display = "none";
        window.location.reload();
    } else {
        alert("Error: " + message);
    }
}

// ===== Fetchers =====
async function runDailyReport() {
  const date = document.getElementById('reportDate').value;
  const token = localStorage.getItem('token');
  if (!date) return alert('Pick a date');
  if (!token) return alert('You are not logged in');

  const url = `${API_BASE_URL}/reports/daily/${date}/${token}}`;
  const res = await fetch(url);
  const data = await res.json();
  if (!res.ok) return alert(data.error || 'Failed to load daily report');
  renderDailyTable(data.rows || []);
}

async function runTopByMonth() {
  const m = Number(document.getElementById('reportMonth').value);
  const y = Number(document.getElementById('reportYear').value);
  const token = localStorage.getItem('token');
  if (!m || !y) return alert('Provide month and year');
  if (!token) return alert('You are not logged in');

  const url = `${API_BASE_URL}/reports/top-doctor/month/${m}/${y}/${token}`;
  const res = await fetch(url);
  const data = await res.json();
  if (!res.ok) return alert(data.error || 'Failed to load month report');
  renderTopDocTable(data.rows || [], `Top Doctor — ${m}/${y}`);
}

async function runTopByYear() {
  const y = Number(document.getElementById('reportYearOnly').value);
  const token = localStorage.getItem('token');
  if (!y) return alert('Provide year');
  if (!token) return alert('You are not logged in');

  const url = `${API_BASE_URL}/reports/top-doctor/year/${y}/${token}`;
  const res = await fetch(url);
  const data = await res.json();
  if (!res.ok) return alert(data.error || 'Failed to load year report');
  renderTopDocTable(data.rows || [], `Top Doctor — ${y}`);
}

// ===== Renderers (into the SAME #content area) =====
function renderDailyTable(rows) {
  const content = document.getElementById('content');
  if (!rows.length) {
    content.innerHTML = `<p>No appointments for selected date.</p>`;
    return;
  }
  content.innerHTML = `
    <table class="report-table">
      <thead class="table-header">
        <tr>
          <th>Doctor</th>
          <th>Appointment Time</th>
          <th>Status</th>
          <th>Patient</th>
          <th>Phone</th>
        </tr>
      </thead>
      <tbody>
        ${rows.map(r => `
          <tr>
            <td>${r.doctorName ?? ''}</td>
            <td>${(r.appointmentTime ?? '').toString().replace('T',' ')}</td>
            <td>${r.status ?? ''}</td>
            <td>${r.patientName ?? ''}</td>
            <td>${r.patientPhone ?? ''}</td>
          </tr>`).join('')}
      </tbody>
    </table>
  `;
}

function renderTopDocTable(rows, title) {
  const content = document.getElementById('content');
  if (!rows.length) {
    content.innerHTML = `<p>No data.</p>`;
    return;
  }
  content.innerHTML = `
    <table class="report-table">
      <thead class="table-header">
        <tr>
          <th>Doctor ID</th>
          <th>Name</th>
          <th>Patients Seen</th>
        </tr>
      </thead>
      <tbody>
        ${rows.map(r => `
          <tr>
            <td>${r.doctorId ?? ''}</td>
            <td>${r.doctorName ?? ''}</td>
            <td>${r.patientsSeen ?? ''}</td>
          </tr>`).join('')}
      </tbody>
    </table>
  `;
}
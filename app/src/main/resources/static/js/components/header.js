// header.js

/*
 * Renders the header depending on the user role and authentication status 
 */
function renderHeader() {
  const headerDiv = document.getElementById("header");
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  if (window.location.pathname.endsWith("/")) {
    localStorage.removeItem("userRole")
    headerDiv.innerHTML = `
      <header class="header">
        <a href="/" class="logo-link" style="text-decoration: none;">
          <div class="logo-section">
            <img src="../assets/images/logo/logo.png" alt="Smart Clinic logo" class="logo-img">
            <span class="logo-title">Smart Clinic</span>
          </div>
        </a>
      </header>`;
    return;
  }

  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Smart Clinic logo" class="logo-img">
        <span class="logo-title">Smart Clinic</span>
      </div>
      <nav>`;
  
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  } else if (role === "admin") {
    headerContent += `
           <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
           <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
           <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
           <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
           <button id="patientLogin" class="adminBtn">Login</button>
           <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
           <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
           <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
           <a href="#" onclick="logoutPatient()">Logout</a>`;
  }

  headerContent += `</nav></header>`;

  headerDiv.innerHTML = headerContent;
  attachHeaderButtonListeners();
}

/*
 * Attaches listeners to the dynamically created header buttons
 */
function attachHeaderButtonListeners() {
  const doctorBtn = document.getElementById("doctorBtn");
  const adminBtn = document.getElementById("adminBtn");
  
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      localStorage.removeItem("token");
      openModal("doctorLogin");
    });
  }

  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      localStorage.removeItem("token");
      openModal("adminLogin");
    });
  }
}

/*
 * Simple logout and redirect function
 */
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

/*
 * Logs patient out and redirects to patient dashboard
 */
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

renderHeader();
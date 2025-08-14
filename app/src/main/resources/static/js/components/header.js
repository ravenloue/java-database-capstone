// header.js

/**
 * Renders the header based on user role and authentication status
 * 
 * Checks localStorage for user role and token to determine which header
 * variation to display. Handles different layouts for public pages,
 * admin dashboard, doctor dashboard, and patient portals.
 * 
 * @function renderHeader
 */
function renderHeader() {
  const headerDiv = document.getElementById("header");
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Special case: homepage gets minimal header with just logo
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

  // Build base header structure with logo
  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <img src="../assets/images/logo/logo.png" alt="Smart Clinic logo" class="logo-img">
        <span class="logo-title">Smart Clinic</span>
      </div>
      <nav>`;
  
  // Validate authenticated users have valid tokens
  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  } else if (role === "admin") {
    // Admin header: Add Doctor button and logout
    headerContent += `
           <button id="addDocBtn" class="adminBtn">Add Doctor</button>
           <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "doctor") {
    // Doctor header: Home button and logout
    headerContent += `
           <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
           <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "patient") {
    // Non-logged patient: Login and signup options
    headerContent += `
           <button id="patientLogin" class="adminBtn">Login</button>
           <button id="patientSignup" class="adminBtn">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    // Logged-in patient: Home, appointments, and logout
    headerContent += `
           <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
           <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
           <a href="#" onclick="logoutPatient()">Logout</a>`;
  }

  headerContent += `</nav></header>`;

  headerDiv.innerHTML = headerContent;
  attachHeaderButtonListeners();
}

/**
 * Attaches event listeners to dynamically created header buttons
 * 
 * Sets up click handlers for doctor and admin login buttons that appear
 * in certain header configurations. Clears any existing tokens before
 * showing login modals to ensure clean authentication flow.
 * 
 * @function attachHeaderButtonListeners
 */
function attachHeaderButtonListeners() {
  const doctorBtn = document.getElementById("doctorBtn");
  const adminBtn = document.getElementById("adminBtn");
  const addDocBtn = document.getElementById("addDocBtn");
  
  // Doctor login button handler
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => {
      localStorage.removeItem("token");
      openModal("doctorLogin");
    });
  }

  // Admin login button handler
  if (adminBtn) {
    adminBtn.addEventListener("click", () => {
      localStorage.removeItem("token");
      openModal("adminLogin");
    });
  }
}

/**
 * Logs out the current user and redirects to homepage
 * 
 * Clears authentication token and user role from localStorage, then
 * redirects to the application root. Used for admin and doctor logout.
 * 
 * @function logout
 */
function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

/**
 * Logs out patient user and redirects to patient dashboard
 * 
 * Specialized logout for patient users that clears credentials but
 * redirects to the public patient dashboard instead of homepage.
 * Sets role back to 'patient' for appropriate header rendering.
 * 
 * @function logoutPatient
 */
function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

// Initialize header on page load
renderHeader();
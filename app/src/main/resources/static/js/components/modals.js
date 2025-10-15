// modals.js

/**
 * Opens a modal dialog with content specific to the requested type.
 * 
 * Dynamically generates HTML content for different modal types including
 * patient login/signup, admin/doctor login, and add doctor forms. Sets
 * up event listeners for form submissions and close button. Modal types
 * supported: addDoctor, patientLogin, patientSignup, adminLogin,
 * doctorLogin.
 * 
 * @export
 * @param {string} type - The type of modal to display
 * @param {Object} [data=null] - Optional data to pre-fill form fields
 * @throws {Error} Will throw error if modal-body element not found
 */
export function openModal(type, data = null) {
  let modalContent = '';

  // Generate modal content based on type parameter
  if (type === 'addDoctor' || type === 'updateDoctor') {
    const isUpdate = type === 'updateDoctor' && data;
    const modalTitle = isUpdate ? 'Update Doctor' : 'Add Doctor';
    const buttonText = isUpdate ? 'Update' : 'Save';
    const buttonId = isUpdate ? 'updateDoctorBtn' : 'saveDoctorBtn';
    
    // Pre-populate fields if updating
    const doctorName = isUpdate ? data.name : '';
    const doctorEmail = isUpdate ? data.email : '';
    const doctorPhone = isUpdate ? data.phone || '' : '';
    const doctorSpecialty = isUpdate ? data.specialty : '';
    const availableTimes = isUpdate ? data.availableTimes : [];


    // Administrator form for adding new doctors to the system
    modalContent = `
         <h2>${modalTitle}</h2>
         ${isUpdate ? `<input type="hidden" id="doctorId" value="${data.id}">` : ''}
         <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field" value="${doctorName}">
         <select id="specialization" class="input-field select-dropdown">
             <option value="" ${doctorSpecialty === '' ? 'selected' : ''}>Specialization</option>
             <option value="Cardiologist" ${doctorSpecialty === 'Cardiologist' ? 'selected' : ''}>Cardiologist</option>
             <option value="Dermatologist" ${doctorSpecialty === 'Dermatologist' ? 'selected' : ''}>Dermatologist</option>
             <option value="Neurologist" ${doctorSpecialty === 'Neurologist' ? 'selected' : ''}>Neurologist</option>
             <option value="Pediatrician" ${doctorSpecialty === 'Pediatrician' ? 'selected' : ''}>Pediatrician</option>
             <option value="Orthopedic" ${doctorSpecialty === 'Orthopedic' ? 'selected' : ''}>Orthopedic</option>
             <option value="Gynecologist" ${doctorSpecialty === 'Gynecologist' ? 'selected' : ''}>Gynecologist</option>
             <option value="Psychiatrist" ${doctorSpecialty === 'Psychiatrist' ? 'selected' : ''}>Psychiatrist</option>
             <option value="Dentist" ${doctorSpecialty === 'Dentist' ? 'selected' : ''}>Dentist</option>
             <option value="Ophthalmologist" ${doctorSpecialty === 'Ophthalmologist' ? 'selected' : ''}>Ophthalmologist</option>
             <option value="ENT" ${doctorSpecialty === 'ENT' ? 'selected' : ''}>ENT Specialist</option>
             <option value="Urologist" ${doctorSpecialty === 'Urologist' ? 'selected' : ''}>Urologist</option>
             <option value="Oncologist" ${doctorSpecialty === 'Oncologist' ? 'selected' : ''}>Oncologist</option>
             <option value="Gastroenterologist" ${doctorSpecialty === 'Gastroenterologist' ? 'selected' : ''}>Gastroenterologist</option>
             <option value="General" ${doctorSpecialty === 'General' ? 'selected' : ''}>General Physician</option>
        </select>
        <input type="email" id="doctorEmail" placeholder="Email" class="input-field" value="${doctorEmail}">
        ${ isUpdate ? '': `<input type="password" id="doctorPassword" placeholder="Password" class="input-field">`}
        <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field" value="${doctorPhone}">
        <div class="availability-container">
        <label class="availabilityLabel">Select Availability:</label>
          <div class="checkbox-group">
              <label><input type="checkbox" name="availability" value="09:00-10:00" 
                      ${availableTimes.includes('09:00-10:00') ? 'checked' : ''}> 9:00 AM - 10:00 AM</label>
              <label><input type="checkbox" name="availability" value="10:00-11:00" 
                      ${availableTimes.includes('10:00-11:00') ? 'checked' : ''}> 10:00 AM - 11:00 AM</label>
              <label><input type="checkbox" name="availability" value="11:00-12:00" 
                      ${availableTimes.includes('11:00-12:00') ? 'checked' : ''}> 11:00 AM - 12:00 PM</label>
              <label><input type="checkbox" name="availability" value="12:00-13:00" 
                      ${availableTimes.includes('12:00-13:00') ? 'checked' : ''}> 12:00 PM - 1:00 PM</label>
              <label><input type="checkbox" name="availability" value="13:00-14:00" 
                      ${availableTimes.includes('13:00-14:00') ? 'checked' : ''}> 1:00 PM - 2:00 PM</label>
              <label><input type="checkbox" name="availability" value="14:00-15:00" 
                      ${availableTimes.includes('14:00-15:00') ? 'checked' : ''}> 2:00 PM - 3:00 PM</label>
              <label><input type="checkbox" name="availability" value="15:00-16:00" 
                      ${availableTimes.includes('15:00-16:00') ? 'checked' : ''}> 3:00 PM - 4:00 PM</label>
              <label><input type="checkbox" name="availability" value="16:00-17:00" 
                      ${availableTimes.includes('16:00-17:00') ? 'checked' : ''}> 4:00 PM - 5:00 PM</label>
          </div>
        </div>
        <button class="dashboard-btn" id="${buttonId}">${buttonText}</button>
      `;
  } else if (type === 'patientLogin') {
    // Patient authentication form
    modalContent = `
        <h2>Patient Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="loginBtn">Login</button>
      `;
  } else if (type === "patientSignup") {
    // New patient registration form
    modalContent = `
      <h2>Patient Signup</h2>
      <input type="text" id="name" placeholder="Name" class="input-field">
      <input type="email" id="email" placeholder="Email" class="input-field">
      <input type="password" id="password" placeholder="Password" class="input-field">
      <input type="text" id="phone" placeholder="Phone" class="input-field">
      <input type="text" id="address" placeholder="Address" class="input-field">
      <button class="dashboard-btn" id="signupBtn">Signup</button>
    `;
  } else if (type === 'adminLogin') {
    // Administrator authentication form
    modalContent = `
        <h2>Admin Login</h2>
        <input type="text" id="username" name="username" placeholder="Username" class="input-field">
        <input type="password" id="password" name="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="adminLoginBtn" >Login</button>
      `;
  } else if (type === 'doctorLogin') {
    // Doctor authentication form
    modalContent = `
        <h2>Doctor Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="doctorLoginBtn" >Login</button>
      `;
  }

   /**
   * Modal Display and Close Handler
   * 
   * Injects generated content into modal body and makes modal visible.
   * Attaches click handler to close button for hiding modal.
   */
  document.getElementById('modal-body').innerHTML = modalContent;
  document.getElementById('modal').style.display = 'block';

  document.getElementById('closeModal').onclick = (e) => {
	e.stopImmediatePropagation();
    document.getElementById('modal').style.display = 'none';
  };

  /**
   * Event Handler Attachments
   * 
   * Connects form submission buttons to their respective handler
   * functions based on modal type. Handlers are defined in respective
   * modules (patientServices, adminDashboard, etc.).
   */
  if (type === "patientSignup") {
    document.getElementById("signupBtn").addEventListener("click", signupPatient);
  }

  if (type === "patientLogin") {
    document.getElementById("loginBtn").addEventListener("click", loginPatient);
  }

  if (type === 'addDoctor') {
    document.getElementById('saveDoctorBtn').addEventListener('click', adminAddDoctor);
  }

  if (type === 'updateDoctor') {
    document.getElementById('updateDoctorBtn').addEventListener("click", adminUpdateDoctor);
  }

  if (type === 'adminLogin') {
    document.getElementById('adminLoginBtn').addEventListener('click', adminLoginHandler);
  }

  if (type === 'doctorLogin') {
    document.getElementById('doctorLoginBtn').addEventListener('click', doctorLoginHandler);
  }
}

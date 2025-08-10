// index.js
import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login'

// Button Event Listeners
window.onload = function () {
    const adminBtn = document.getElementById("admin-btn");
    const doctorBtn = document.getElementById("doctor-btn");

    if (adminBtn) {
        adminBtn.addEventListener("click", () => {
            openModal("adminLogin");
        });
    }
    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
            openModal("doctorLogin");
        });
    }
}

// Admin Login handler
window.adminLoginHandler = async function () {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const admin = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
            method: "POST", 
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            localStorage.setItem("token", result.token);
            selectRole("admin");
        } else {
            alert("Invalid credentials.");
        }
    } catch (error) {
        console.error("Login failed: ", error);
        alert("Something went wrong!?");
    }
};

// Doctor Login handler
window.doctorLoginHandler = async function () {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const doctor = { username, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: "POST", 
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            const result = await response.json();
            localStorage.setItem("token", result.token);
            selectRole("doctor");
        } else {
            alert("Invalid credentials.");
        }
    } catch (error) {
        console.error("Login failed: ", error);
        alert("Something went wrong!?");
    }
};
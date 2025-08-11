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
            console.log("admin clicked")
            openModal("adminLogin");
        });
    }
    if (doctorBtn) {
        doctorBtn.addEventListener("click", () => {
            console.log("doctor clicked")
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
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const result = await response.json();
            localStorage.setItem("token", result.token);
            setRole("admin");
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
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const doctor = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: "POST", 
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const result = await response.json();
            localStorage.setItem("token", result.token);
            setRole("doctor");
        } else {
            alert("Invalid credentials.");
        }
    } catch (error) {
        console.error("Login failed: ", error);
        alert("Something went wrong!?");
    }
};
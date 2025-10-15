// doctorServices.js
import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

/**
 * Fetches all doctors from the API.
 * 
 * Makes GET request to doctor endpoint without authentication. Returns
 * array of doctor objects or empty array on error. Used for public
 * doctor listing on admin dashboard.
 * 
 * @async
 * @returns {Promise<Array>} Array of doctor objects
 */
export async function getDoctors() {
    try {
      const response = await fetch(DOCTOR_API);
      const data = await response.json();
      return data.doctors;
    } catch (error) {
      console.error("Error fetching doctors: ", error);
      return [];
    }
  }

/**
 * Deletes a doctor by ID with admin authentication.
 * 
 * Sends DELETE request with doctor ID and admin token. Returns success
 * status and message for UI feedback. Requires valid admin token for
 * authorization.
 * 
 * @async
 * @param {number} id - Doctor ID to delete
 * @param {string} token - Admin JWT token for authentication
 * @returns {Promise<Object>} Object with success boolean and message
 */
export async function deleteDoctor(id, token) {
    try {
        const response = await fetch(`${DOCTOR_API}/${id}/${token}`, 
        { method: "DELETE", });

        const result = await response.json();
        return { success: response.ok, message: result.message };
    } catch (error) {
        console.error("Error deleting doctor: ", error);
        return { success: false, message: "Server error" };
    }
}

/**
 * Updates an existing doctor by ID with admin privileges.
 *
 * @async
 * @param {number|string} id - Doctor ID to update
 * @param {Object} doctor - Updated doctor payload
 * @param {string} token - Admin JWT token for authentication
 * @returns {Promise<Object>} Object with success boolean and message
 */
export async function updateDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "PATCH",
      headers: { "Content-type": "application/json" },
      body: JSON.stringify(doctor),
    });
    const result = await response.json();
    return { success: response.ok, message: result.message };
  } catch (error) {
    console.error("Error updating doctor: ", error);
    return { success: false, message: "Server error" };
  }
}

/**
 * Creates a new doctor account with admin privileges.
 * 
 * Posts doctor data including credentials and availability. Validates
 * admin token before processing. Returns operation status and message
 * for user feedback.
 * 
 * @async
 * @param {Object} doctor - Doctor object with all required fields
 * @param {string} token - Admin JWT token for authentication
 * @returns {Promise<Object>} Object with success boolean and message
 */
export async function saveDoctor(doctor, token){
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, 
    {
      method: "POST",
      headers: { "Content-type": "application/json" },
      body:JSON.stringify(doctor)
    });
    const result = await response.json();
    return {success: response.ok , message: result.message}
    }
  catch(error) {
    console.error("Error saving doctor: ", error);
    return { success: false, message: result.message }
  }
}

/**
 * Filters doctors by multiple criteria.
 * 
 * Applies name search (partial match), time availability, and specialty
 * filters. Sends "null" string for empty filters to backend. Returns
 * filtered doctor list or empty array on error.
 * 
 * @async
 * @param {string|null} name - Doctor name to search (partial match)
 * @param {string|null} time - Available time slot filter
 * @param {string|null} specialty - Medical specialty filter
 * @returns {Promise<Object>} Object containing doctors array
 */
export async function filterDoctors(name, time, specialty) {
    try {
      const response = await fetch(`${DOCTOR_API}/filter/${name}/${time}/${specialty}`, 
      {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      });
  
      if (response.ok) {
        const data = await response.json();
        return data; 
        
      } else {
        console.error("Failed to fetch doctors: ", response.statusText);
        return { doctors: [] };
        
      }
    } catch (error) {
      console.error("Error: ", error);
      alert("Something went wrong!");
      return { doctors: [] }; 
    }
}
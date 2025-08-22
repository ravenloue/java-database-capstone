// appointmentRecordService.js
import { API_BASE_URL } from "../config/config.js";
const APPOINTMENT_API = API_BASE_URL + "/appointments";


/**
 * Fetches all appointments for a doctor on a specific date.
 * 
 * Retrieves appointments from the API filtered by date and optionally by
 * patient name. Constructs URL with path parameters for date, patient name
 * filter, and authentication token.
 * 
 * @async
 * @param {string} date - Date in YYYY-MM-DD format to filter appointments
 * @param {string|null} patientName - Patient name for filtering or null
 * @param {string} token - JWT authentication token for doctor verification
 * @returns {Promise<Object>} JSON object with appointments array
 * @throws {Error} When API response is not successful (non-2xx status)
 */
export async function getAllAppointments(date, patientName, token) {
  const nameParam = patientName || null;
  const url = `${APPOINTMENT_API}/${date}/${nameParam}/${token}`;
  
  try {
    const response = await fetch(url);
    const text = await response.text();
    
    if (!response.ok) {
      throw new Error(`Failed to fetch appointments: ${response.status} - ${text}`);
    }
    
    return JSON.parse(text);
  } catch (error) {
    console.error("Fetch error:", error);
    throw error;
  }
}

/**
 * Books a new appointment for a patient.
 * 
 * Sends appointment data to the API to create a new appointment record.
 * Includes patient ID, doctor ID, and appointment time in the request body.
 * Returns success status and message for UI feedback.
 * 
 * @async
 * @param {Object} appointment - Appointment object with patient, doctor, time
 * @param {string} token - JWT authentication token for patient verification
 * @returns {Promise<Object>} Object with success boolean and message string
 */
export async function bookAppointment(appointment, token) {
    
  try {
    const response = await fetch(`${APPOINTMENT_API}/${token}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong"
    };
  } catch (error) {
    console.error("Error while booking appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}

/**
 * Updates an existing appointment.
 * 
 * Sends updated appointment data to modify an existing appointment record.
 * Used for rescheduling or updating appointment details. Validates that the
 * patient ID matches the original appointment for security.
 * 
 * @async
 * @param {Object} appointment - Updated appointment object with ID
 * @param {string} token - JWT authentication token for patient verification
 * @returns {Promise<Object>} Object with success boolean and message string
 */
export async function updateAppointment(appointment, token) {
  try {
    const response = await fetch(`${APPOINTMENT_API}/${token}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(appointment)
    });

    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Something went wrong"
    };
  } catch (error) {
    console.error("Error while booking appointment:", error);
    return {
      success: false,
      message: "Network error. Please try again later."
    };
  }
}

export async function getUpcomingAppointments(token, patientName = null) {
  const base = `${API_BASE_URL}/appointments/upcoming/${token}`;
  const url = patientName ? `${base}?patientName=${encodeURIComponent(patientName)}` : base;

  const res = await fetch(url, { headers: { "Accept": "application/json" } });
  if (!res.ok) {
    const err = await res.text().catch(() => "");
    throw new Error(`Upcoming fetch failed: ${res.status} ${err}`);
  }
  return res.json();
}

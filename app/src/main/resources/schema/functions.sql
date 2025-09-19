CREATE OR REPLACE FUNCTION get_daily_appointment_report_by_doctor(
  report_date date
)
RETURNS TABLE (
  doctor_name     text,
  appointment_time timestamp,
  status          integer,
  patient_name    text,
  patient_phone   text
)
LANGUAGE sql
AS $$
  SELECT
    d.name  AS doctor_name,
    a.appointment_time,
    a.status,
    p.name  AS patient_name,
    p.phone AS patient_phone
  FROM appointment a
  JOIN doctor d  ON a.doctor_id  = d.id
  JOIN patient p ON a.patient_id = p.id
  WHERE a.appointment_time::date = report_date
  ORDER BY d.name, a.appointment_time;
$$;

CREATE OR REPLACE FUNCTION get_doctor_with_most_patients_by_month(
  input_month int,
  input_year  int
)
RETURNS TABLE(
  doctor_id    int,
  doctor_name  text,
  patients_seen bigint
)
LANGUAGE sql
AS $$
  SELECT
    a.doctor_id,
    d.name AS doctor_name,
    COUNT(a.patient_id) AS patients_seen
  FROM appointment a
  JOIN doctor d ON d.id = a.doctor_id
  WHERE EXTRACT(MONTH FROM a.appointment_time) = input_month
    AND EXTRACT(YEAR  FROM a.appointment_time) = input_year
  GROUP BY a.doctor_id, d.name
  ORDER BY patients_seen DESC
  LIMIT 1;
$$;

CREATE OR REPLACE FUNCTION get_doctor_with_most_patients_by_year(
  input_year int
)
RETURNS TABLE (
  doctor_id     bigint,
  doctor_name   text,
  patients_seen bigint
)
LANGUAGE sql
AS $$
  SELECT
    a.doctor_id,
    d.name AS doctor_name,
    COUNT(a.patient_id) AS patients_seen
  FROM appointment a
  JOIN doctor d ON d.id = a.doctor_id
  WHERE EXTRACT(YEAR FROM a.appointment_time) = input_year
  GROUP BY a.doctor_id, d.name
  ORDER BY patients_seen DESC
  LIMIT 1;
$$;

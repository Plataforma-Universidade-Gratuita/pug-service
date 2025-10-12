CREATE
OR REPLACE VIEW v_students_progress AS
SELECT s.id,
       s.user_id,
       s.academic_registration,
       s.required_hours,
       s.completed_hours,
       GREATEST(s.required_hours - s.completed_hours, 0)                 AS remaining_hours,
       (CURRENT_DATE BETWEEN s.start_date AND s.due_date)                AS in_window,
       ROUND((s.completed_hours * 100) / NULLIF(s.required_hours, 0), 2) AS progress_pct,
       s.start_date,
       s.due_date,
       s.active
FROM students s;

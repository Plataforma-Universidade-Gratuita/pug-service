CREATE
OR REPLACE VIEW v_student_hours AS
SELECT a.student_id,
       COALESCE(SUM(CASE WHEN a.status = 'VALIDATED' THEN a.duration ELSE 0 END), 0) AS completed_hours
FROM attendances a
GROUP BY a.student_id;

CREATE
OR REPLACE VIEW v_students_progress AS
SELECT s.user_id                                                      AS student_user_id,
       s.academic_registration,
       s.required_hours,
       COALESCE(h.completed_hours, 0)                                 AS completed_hours,
       GREATEST(s.required_hours - COALESCE(h.completed_hours, 0), 0) AS remaining_hours,
       (CURRENT_DATE BETWEEN s.start_date AND s.due_date)             AS in_window,
       CASE
           WHEN s.required_hours <> 0
               THEN ROUND((COALESCE(h.completed_hours, 0) * 100) / s.required_hours, 2)
           ELSE NULL END                                              AS progress_pct,
       s.start_date,
       s.due_date,
       s.campus,
       s.course_id
FROM students s
         LEFT JOIN v_student_hours h ON h.student_id = s.user_id;

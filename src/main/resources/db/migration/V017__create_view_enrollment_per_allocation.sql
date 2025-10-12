CREATE
OR REPLACE VIEW v_enrollments AS
SELECT e.id                                               AS enrollment_id,
       e.status                                           AS enrollment_status,
       e.request_at,
       e.accepted_at,
       e.closing_status_at,

       e.allocation_id,
       a.project_id,
       a.start_date                                       AS allocation_start_date,
       a.end_date                                         AS allocation_end_date,
       (CURRENT_DATE BETWEEN a.start_date AND a.end_date) AS allocation_in_window,
       a.max_participants                                 AS allocation_max_participants,

       p.name                                             AS project_name,
       p.status                                           AS project_status,

       e.student_id,
       s.user_id                                          AS student_user_id,
       s.academic_registration,
       s.course_id,
       s.required_hours,
       s.completed_hours,
       GREATEST(s.required_hours - s.completed_hours, 0)  AS student_remaining_hours,
       s.completed                                        AS student_completed
FROM enrollments e
         JOIN allocations a ON a.id = e.allocation_id
         JOIN projects p ON p.id = a.project_id
         JOIN students s ON s.id = e.student_id;

CREATE
OR REPLACE VIEW v_allocation_progress AS
WITH e AS (
  SELECT allocation_id,
         COUNT(*) FILTER (WHERE status IN ('ACCEPTED','COMPLETED')) AS accepted_participants
  FROM enrollments
  GROUP BY allocation_id
)
SELECT a.id,
       a.project_id,
       a.offered_hours,
       a.allocated_hours,
       GREATEST(a.offered_hours - a.allocated_hours, 0)                 AS available_hours,
       (CURRENT_DATE::date <@ a.period)                                 AS in_window,
       ROUND((a.allocated_hours * 100) / NULLIF(a.offered_hours, 0), 2) AS progress_pct,
       a.start_date,
       a.end_date,
       a.status,
       a.max_participants,
       COALESCE(e.accepted_participants, 0)                             AS accepted_participants,
       CASE
           WHEN a.max_participants IS NULL THEN NULL
           ELSE GREATEST(a.max_participants - COALESCE(e.accepted_participants, 0), 0)
           END                                                          AS available_slots
FROM allocations a
         LEFT JOIN e ON e.allocation_id = a.id;

CREATE
OR REPLACE VIEW v_project_progress AS
WITH e AS (
  SELECT e.project_id,
         COUNT(*) FILTER (WHERE e.status IN ('ACCEPTED','COMPLETED')) AS accepted_participants
  FROM enrollments e
  GROUP BY e.project_id
),
alloc AS (
  SELECT a.project_id,
         COALESCE(SUM(CASE WHEN a.status = 'VALIDATED' THEN a.duration ELSE 0 END), 0) AS allocated_hours
  FROM attendances a
  GROUP BY a.project_id
)
SELECT p.id                                                              AS project_id,
       p.name,
       p.entity_id,
       p.status,
       p.created_by_user_id,
       p.created_at,
       p.closed_at,
       p.offered_hours,
       COALESCE(alloc.allocated_hours, 0)                                AS allocated_hours,
       GREATEST(p.offered_hours - COALESCE(alloc.allocated_hours, 0), 0) AS available_hours,
       CASE
           WHEN p.offered_hours <> 0
               THEN ROUND((COALESCE(alloc.allocated_hours, 0) * 100) / p.offered_hours, 2)
           ELSE NULL END                                                 AS progress_pct,
       p.max_participants,
       COALESCE(e.accepted_participants, 0)                              AS accepted_participants,
       CASE
           WHEN p.max_participants IS NULL THEN NULL
           ELSE GREATEST(p.max_participants - COALESCE(e.accepted_participants, 0), 0)
           END                                                           AS available_slots
FROM projects p
         LEFT JOIN e ON e.project_id = p.id
         LEFT JOIN alloc ON alloc.project_id = p.id;

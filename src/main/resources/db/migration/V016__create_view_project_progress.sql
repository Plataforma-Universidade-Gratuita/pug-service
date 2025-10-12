CREATE
OR REPLACE VIEW v_project_progress AS
WITH a_agg AS (
  SELECT
    a.project_id,
    COALESCE(SUM(a.offered_hours), 0)   AS offered_hours_total,
    COALESCE(SUM(a.allocated_hours), 0) AS allocated_hours_total,
    COUNT(*) FILTER (WHERE CURRENT_DATE::date <@ a.period) AS active_allocations,
    MIN(a.start_date) AS first_start_date,
    MAX(a.end_date)   AS last_end_date
  FROM allocations a
  GROUP BY a.project_id
)
SELECT p.id                                  AS project_id,
       p.name,
       p.status,
       p.entity_id,
       p.created_at,
       COALESCE(aa.offered_hours_total, 0)   AS offered_hours_total,
       COALESCE(aa.allocated_hours_total, 0) AS allocated_hours_total,
       GREATEST(
               COALESCE(aa.offered_hours_total, 0) - COALESCE(aa.allocated_hours_total, 0),
               0
       )                                     AS available_hours_total,
       COALESCE(
               (COALESCE(aa.allocated_hours_total, 0) * 100)
                   / NULLIF(COALESCE(aa.offered_hours_total, 0), 0),
               0
       )                                     AS progress_pct,
       COALESCE(aa.active_allocations, 0)    AS active_allocations,
       aa.first_start_date,
       aa.last_end_date
FROM projects p
         LEFT JOIN a_agg aa ON aa.project_id = p.id;

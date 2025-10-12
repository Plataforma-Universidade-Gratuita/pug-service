CREATE
OR REPLACE FUNCTION sync_student_completed_hours()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
v_student uuid;
BEGIN
  IF
TG_OP = 'DELETE' THEN
SELECT s.id
INTO v_student
FROM enrollments e
         JOIN students s ON s.id = e.student_id
WHERE e.id = OLD.enrollment_id;
ELSE
SELECT s.id
INTO v_student
FROM enrollments e
         JOIN students s ON s.id = e.student_id
WHERE e.id = NEW.enrollment_id;
END IF;

UPDATE students s
SET completed_hours = LEAST(
        s.required_hours,
        (SELECT COALESCE(SUM(a2.duration), 0)
         FROM attendances a2
                  JOIN enrollments e2 ON e2.id = a2.enrollment_id
         WHERE e2.student_id = s.id
           AND a2.status = 'VALIDATED')
                      )
WHERE s.id = v_student;

RETURN COALESCE(NEW, OLD);
END $$;

CREATE TRIGGER trg_sync_student_completed_hours_ins
    AFTER INSERT
    ON attendances
    FOR EACH ROW EXECUTE FUNCTION sync_student_completed_hours();

CREATE TRIGGER trg_sync_student_completed_hours_upd
    AFTER UPDATE OF status, duration, enrollment_id
    ON attendances
    FOR EACH ROW EXECUTE FUNCTION sync_student_completed_hours();

CREATE TRIGGER trg_sync_student_completed_hours_del
    AFTER DELETE
    ON attendances
    FOR EACH ROW EXECUTE FUNCTION sync_student_completed_hours();

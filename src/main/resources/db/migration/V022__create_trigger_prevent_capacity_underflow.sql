CREATE
OR REPLACE FUNCTION prevent_capacity_underflow()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
v_cnt int;
BEGIN
  IF
NEW.max_participants IS NULL THEN
    RETURN NEW;
END IF;

  IF
TG_OP = 'INSERT' OR NEW.max_participants <> OLD.max_participants THEN
SELECT COUNT(*)
INTO v_cnt
FROM enrollments e
WHERE e.allocation_id = COALESCE(NEW.id, OLD.id)
  AND e.status IN ('ACCEPTED', 'COMPLETED');

IF
v_cnt > NEW.max_participants THEN
      RAISE EXCEPTION 'max_participants % < already accepted % for allocation %',
        NEW.max_participants, v_cnt, COALESCE(NEW.id, OLD.id)
        USING ERRCODE = 'check_violation';
END IF;
END IF;

RETURN NEW;
END $$;

CREATE TRIGGER trg_prevent_capacity_underflow
    BEFORE INSERT OR
UPDATE OF max_participants
ON allocations
    FOR EACH ROW EXECUTE FUNCTION prevent_capacity_underflow();

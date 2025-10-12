CREATE
OR REPLACE FUNCTION enforce_allocation_capacity()
RETURNS trigger LANGUAGE plpgsql AS $$
DECLARE
v_max int;
  v_cnt
int;
BEGIN
  IF
NEW.status IN ('ACCEPTED','COMPLETED') THEN
    -- lock parent to prevent race
SELECT max_participants
INTO v_max
FROM allocations
WHERE id = NEW.allocation_id
    FOR UPDATE;

IF
v_max IS NOT NULL THEN
SELECT COUNT(*)
INTO v_cnt
FROM enrollments
WHERE allocation_id = NEW.allocation_id
  AND status IN ('ACCEPTED', 'COMPLETED')
  AND id <> COALESCE(NEW.id, '00000000-0000-0000-0000-000000000000');

IF
v_cnt >= v_max THEN
        RAISE EXCEPTION 'Allocation % has no available slots', NEW.allocation_id
          USING ERRCODE = 'check_violation';
END IF;
END IF;
END IF;
RETURN NEW;
END $$;

CREATE TRIGGER trg_enforce_allocation_capacity
    BEFORE INSERT OR
UPDATE OF status
ON enrollments
    FOR EACH ROW
    EXECUTE FUNCTION enforce_allocation_capacity();

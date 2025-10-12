CREATE
OR REPLACE FUNCTION set_allocation_completed_at()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  IF
NEW.status = 'COMPLETED' AND (OLD.status IS DISTINCT FROM 'COMPLETED') THEN
    NEW.completed_at := COALESCE(NEW.completed_at, now());
  ELSIF
OLD.status = 'COMPLETED' AND NEW.status <> 'COMPLETED' THEN
    NEW.completed_at := NULL; -- optional reset
END IF;
RETURN NEW;
END $$;

CREATE TRIGGER trg_set_allocation_completed_at
    BEFORE UPDATE OF status
    ON allocations
    FOR EACH ROW EXECUTE FUNCTION set_allocation_completed_at();

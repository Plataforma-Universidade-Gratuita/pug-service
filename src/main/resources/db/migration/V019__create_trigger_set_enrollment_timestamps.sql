CREATE
OR REPLACE FUNCTION set_enrollment_timestamps()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  IF
TG_OP = 'INSERT' THEN
    IF NEW.status = 'ACCEPTED' AND NEW.accepted_at IS NULL THEN
      NEW.accepted_at := now();
END IF;
    IF
NEW.status IN ('REJECTED','CANCELLED','COMPLETED') AND NEW.closing_status_at IS NULL THEN
      NEW.closing_status_at := now();
END IF;
ELSE
    IF NEW.status = 'ACCEPTED' AND (OLD.status IS DISTINCT FROM 'ACCEPTED') THEN
      NEW.accepted_at := COALESCE(NEW.accepted_at, now());
END IF;
    IF
NEW.status IN ('REJECTED','CANCELLED','COMPLETED')
       AND (OLD.status IS DISTINCT FROM NEW.status) THEN
      NEW.closing_status_at := COALESCE(NEW.closing_status_at, now());
END IF;
END IF;
RETURN NEW;
END $$;

CREATE TRIGGER trg_set_enrollment_timestamps
    BEFORE INSERT OR
UPDATE OF status
ON enrollments
    FOR EACH ROW EXECUTE FUNCTION set_enrollment_timestamps();

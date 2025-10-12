CREATE
OR REPLACE FUNCTION enforce_attendance_validation()
RETURNS trigger LANGUAGE plpgsql AS $$
BEGIN
  IF
NEW.status = 'VALIDATED' THEN
    IF NEW.validated_by IS NULL THEN
      RAISE EXCEPTION 'validated_by required when status=VALIDATED';
END IF;
    NEW.validated_at
:= COALESCE(NEW.validated_at, now());
END IF;
RETURN NEW;
END $$;

CREATE TRIGGER trg_enforce_attendance_validation
    BEFORE INSERT OR
UPDATE OF status
ON attendances
    FOR EACH ROW EXECUTE FUNCTION enforce_attendance_validation();

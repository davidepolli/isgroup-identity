CREATE OR REPLACE FUNCTION prevent_email_update()
RETURNS trigger AS $$
BEGIN
  IF NEW.email IS DISTINCT FROM OLD.email THEN
    RAISE EXCEPTION 'email is immutable';
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_users_email_immutable ON users;
CREATE TRIGGER trg_users_email_immutable
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION prevent_email_update();
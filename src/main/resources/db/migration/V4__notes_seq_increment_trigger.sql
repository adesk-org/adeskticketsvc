-- Auto-increment seq per (tenant_id, ticket_id)
CREATE OR REPLACE FUNCTION assign_note_seq()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
	new.seq := COALESCE(
		(
			SELECT MAX(seq) + 1
			FROM notes
			WHERE tenant_id = new.tenant_id
				AND ticket_id = new.ticket_id
		), 1
	);
	RETURN new;
END;
$$;

CREATE TRIGGER trg_assign_note_seq BEFORE
INSERT
  ON notes FOR each ROW
  WHEN (new.seq IS NULL) EXECUTE FUNCTION assign_note_seq();

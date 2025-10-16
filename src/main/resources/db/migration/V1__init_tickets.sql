-- Enum for status to match API contract
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'ticket_status') THEN
    CREATE TYPE ticket_status AS ENUM ('OPEN','IN_PROGRESS','RESOLVED','CLOSED');
  END IF;
END$$;

-- tickets table
CREATE TABLE IF NOT EXISTS tickets (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tenant_id UUID NOT NULL,
  title TEXT NOT NULL,
  description TEXT NOT NULL,
  status ticket_status NOT NULL,
  assignee TEXT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Helpful indexes for tenant-scoped queries
CREATE INDEX IF NOT EXISTS idx_tickets_tenant_created ON tickets(tenant_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_tickets_tenant_status ON tickets(tenant_id, status);
CREATE INDEX IF NOT EXISTS idx_tickets_tenant_assignee ON tickets(tenant_id, assignee);

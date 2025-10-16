-- Create notes table
CREATE TABLE IF NOT EXISTS notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    ticket_id UUID NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    seq INT NOT NULL,
    content TEXT NOT NULL,
    author_name TEXT NOT NULL,
    is_private BOOLEAN NOT NULL,
    is_deleted BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_notes_tenant_ticket_seq ON notes(tenant_id, ticket_id, seq);
CREATE INDEX IF NOT EXISTS idx_notes_active ON notes(ticket_id, created_at) WHERE is_deleted = false;

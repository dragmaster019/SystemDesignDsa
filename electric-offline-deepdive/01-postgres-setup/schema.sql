-- 01-postgres-setup/schema.sql — fintech truth tables
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS accounts (
  id TEXT PRIMARY KEY,
  branch_id TEXT NOT NULL,
  balance_paise BIGINT NOT NULL CHECK (balance_paise >= 0),
  version BIGINT NOT NULL DEFAULT 1, -- optimistic lock helper
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_accounts_branch ON accounts(branch_id);

-- Append-only. NEVER UPDATE/DELETE. Corrections = new reversing row.
CREATE TABLE IF NOT EXISTS ledger_entries (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  from_id TEXT NOT NULL REFERENCES accounts(id),
  to_id TEXT NOT NULL REFERENCES accounts(id),
  amount_paise BIGINT NOT NULL CHECK (amount_paise > 0),
  branch_id TEXT NOT NULL,
  status TEXT NOT NULL DEFAULT 'posted', -- posted | reversed
  reverses UUID REFERENCES ledger_entries(id),
  idempotency_key TEXT NOT NULL UNIQUE,
  inserted_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_entries_branch_time ON ledger_entries(branch_id, inserted_at DESC);
CREATE INDEX IF NOT EXISTS idx_entries_from ON ledger_entries(from_id);

INSERT INTO accounts(id, branch_id, balance_paise) VALUES
  ('alice','blr', 10000),
  ('bob','blr', 5000),
  ('agent1','blr', 20000)
ON CONFLICT (id) DO NOTHING;

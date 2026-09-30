-- Postgres schema for fintech + ElectricSQL
-- Run: psql $DATABASE_URL -f schema.sql

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE accounts (
  id TEXT PRIMARY KEY,
  branch_id TEXT NOT NULL,
  balance_paise BIGINT NOT NULL CHECK (balance_paise >= 0),
  updated_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_accounts_branch ON accounts(branch_id);

CREATE TABLE ledger_entries (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  from_id TEXT NOT NULL REFERENCES accounts(id),
  to_id TEXT NOT NULL REFERENCES accounts(id),
  amount_paise BIGINT NOT NULL CHECK (amount_paise > 0),
  branch_id TEXT NOT NULL,
  idempotency_key TEXT NOT NULL UNIQUE, -- critical: safe retries
  inserted_at TIMESTAMPTZ DEFAULT now()
);
CREATE INDEX idx_entries_branch_time ON ledger_entries(branch_id, inserted_at DESC);

-- Electric: enable logical replication + electrify
-- (Electric service does ELECTRIFY automatically for tables in publication;
--  with self-hosted Electric, run:)
-- ALTER TABLE accounts REPLICA IDENTITY FULL;
-- ALTER TABLE ledger_entries REPLICA IDENTITY FULL;

-- Seed for demo
INSERT INTO accounts(id, branch_id, balance_paise) VALUES
  ('alice','branch-blr', 10000),
  ('bob','branch-blr', 5000)
ON CONFLICT (id) DO NOTHING;

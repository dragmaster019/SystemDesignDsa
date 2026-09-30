-- 01-postgres-setup/electrify.sql — what Electric needs
-- Electric requires FULL replica identity so updates/deletes replicate with old row.

ALTER TABLE accounts REPLICA IDENTITY FULL;
ALTER TABLE ledger_entries REPLICA IDENTITY FULL;

-- Optional: dedicated publication (newer Electric auto-electrifies; explicit is safer)
DROP PUBLICATION IF EXISTS electric_pub;
CREATE PUBLICATION electric_pub FOR TABLE accounts, ledger_entries;

-- Verify:
-- SELECT * FROM pg_publication_tables WHERE pubname='electric_pub';
-- SHOW wal_level; -- must be 'logical'

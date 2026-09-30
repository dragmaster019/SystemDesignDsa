# Elixir Fintech Crash Course — From JD to Production-Ready

Derived from your JD:
> Elixir + PostgreSQL/ElectricSQL + real-time local-first sync + own reliability/perf/observability + debug concurrency/distributed state/DB perf + client-facing delivery for real-money fintech.

## How to use this folder (learn fast = build in order)

| Order | Folder | JD skill it covers | Time |
|-------|--------|--------------------|------|
| 1 | `01-elixir-prod-basics` | Strong hands-on production Elixir | 1 day |
| 2 | `02-phoenix-ledger-api` | Own features end-to-end, fast | 1 day |
| 3 | `03-postgres-electricsql-sync` | PostgreSQL / ElectricSQL + real-time sync | 1-2 days |
| 4 | `04-concurrency-distributed` | Concurrency, distributed state | 1 day |
| 5 | `05-observability-reliability` | Reliability, performance, observability | 0.5 day |
| 6 | `06-fintech-domain` | Payments / lending / banking (nice-to-have → must-talk) | 1 day |
| 7 | `07-prod-debugging` | Debug hard production issues | 0.5 day |
| 8 | `08-client-delivery` | Forward-deployed, stakeholder comms, scoping + risks | 0.5 day |

Total: ~7 days at 3-4 hrs/day.

## Run everything locally

```bash
# 1. Install Elixir (macOS)
brew install elixir postgresql

# 2. Check
elixir --version
mix --version

# 3. Run the no-deps demos (works without Phoenix/Postgres)
elixir elixir-fintech-crash-course/01-elixir-prod-basics/ledger_core.exs
elixir elixir-fintech-crash-course/04-concurrency-distributed/transfer_server.exs
elixir elixir-fintech-crash-course/06-fintech-domain/double_entry.exs

# 4. Full stack (Phoenix + Postgres + Electric)
cd elixir-fintech-crash-course/03-postgres-electricsql-sync
docker compose up -d
```

## Mental model for interview

Client wants someone who can say:
1. "Money never disappears — I use double-entry + DB transactions + idempotency keys."
2. "Real-time sync = Postgres is source of truth, ElectricSQL syncs shapes to edge/client, Phoenix handles writes."
3. "I observe with Telemetry + OpenTelemetry + structured logs, and I can debug BEAM + slow queries in prod."
4. "I scope fast, flag risks early (consistency vs offline-write conflicts), and talk to product/business without jargon."

Start with `01-elixir-prod-basics/README.md`.

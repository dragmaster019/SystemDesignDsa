// 04-client-offline/client_offline.js — local-first branch app
// npm i @electric-sql/client better-sqlite3
// node client_offline.js
import { ShapeStream, Shape } from "@electric-sql/client";
import Database from "better-sqlite3";

const db = new Database(":memory:");
db.exec(`
  CREATE TABLE ledger_entries(id TEXT PRIMARY KEY, from_id TEXT, to_id TEXT, amount_paise INT);
  CREATE TABLE pending_outbox(idempotency_key TEXT PRIMARY KEY, from_id TEXT, to_id TEXT, amount_paise INT, status TEXT DEFAULT 'queued', attempts INT DEFAULT 0);
`);

const API = "http://localhost:4000/api/transfers";
// Proxy shapes through Phoenix in prod so branch_id comes from auth:
const SHAPE_URL = "http://localhost:5133/v1/shape";

// 1. Live subscription — overwrites local mirror (truth flows down)
const stream = new ShapeStream({ url: SHAPE_URL, params: { table: "ledger_entries" } });
const shape = new Shape(stream);
shape.subscribe((data) => {
  const insert = db.prepare(`INSERT OR REPLACE INTO ledger_entries VALUES (?,?,?,?)`);
  for (const [, r] of data.rows) insert.run(r.id, r.from_id, r.to_id, Number(r.amount_paise));
  console.log(`[shape] synced ${data.rows.size} rows`);
});

// 2. Pay: try server now, else queue offline with SAME idempotency key
export async function pay(from, to, amount_paise) {
  const key = crypto.randomUUID();
  db.prepare(`INSERT INTO pending_outbox VALUES (?,?,?,?, 'queued', 0)`)
    .run(key, from, to, amount_paise);
  await flushOutbox();
}

async function postOne(row) {
  const res = await fetch(API, {
    method: "POST",
    headers: { "Content-Type": "application/json", "Idempotency-Key": row.idempotency_key },
    body: JSON.stringify({ from: row.from_id, to: row.to_id, amount_paise: row.amount_paise })
  });
  return res;
}

// 3. Outbox worker: safe to retry — same key returns 200 duplicate, never double-charges
export async function flushOutbox() {
  const rows = db.prepare(`SELECT * FROM pending_outbox WHERE status='queued'`).all();
  for (const row of rows) {
    try {
      const res = await postOne(row);
      if (res.status === 201 || res.status === 200) {
        db.prepare(`DELETE FROM pending_outbox WHERE idempotency_key=?`).run(row.idempotency_key);
        console.log(`[outbox] ${row.idempotency_key} confirmed (${res.status})`);
      } else if (res.status === 422) {
        const body = await res.text();
        db.prepare(`UPDATE pending_outbox SET status='failed' WHERE idempotency_key=?`).run(row.idempotency_key);
        console.log(`[outbox] REJECTED ${row.idempotency_key}: ${body} — show FAILED in UI, refresh balance`);
      }
    } catch (e) {
      // offline — leave queued, backoff and retry later
      db.prepare(`UPDATE pending_outbox SET attempts = attempts + 1 WHERE idempotency_key=?`).run(row.idempotency_key);
      console.log(`[outbox] offline, kept ${row.idempotency_key} queued`);
    }
  }
}

setInterval(flushOutbox, 5000); // background retry every 5s
console.log("client running: pay() queues offline-safe transfers; shape syncs truth down");

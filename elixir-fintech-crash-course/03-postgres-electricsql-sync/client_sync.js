// npm i @electric-sql/client — subscribes to branch's ledger shape (local-first)
import { ShapeStream, Shape } from "@electric-sql/client";

const stream = new ShapeStream({
  url: "http://localhost:5133/v1/shape",
  params: { table: "ledger_entries", where: `branch_id = 'branch-blr'` }
});

const shape = new Shape(stream);

shape.subscribe((data) => {
  console.log(`[sync] ${data.rows.size} entries synced`);
  for (const [, row] of data.rows)
    console.log(`  ${row.from_id} -> ${row.to_id} : ${row.amount_paise} paise`);
});

// Offline rule: render from local shape immediately,
// but POST writes to Phoenix API (never write ledger directly).
async function pay(from, to, amount_paise) {
  await fetch("http://localhost:4000/api/transfers", {
    method: "POST",
    headers: { "Content-Type": "application/json", "Idempotency-Key": crypto.randomUUID() },
    body: JSON.stringify({ from, to, amount_paise })
  });
  // Electric shape will push the committed row back within ~100ms
}

# Doc 2 — Talking Points on My Actual Work

Two systems to be ready to discuss: **(A) Account Review Dashboard** (the UI/API
flow) and **(B) AUM Quarterly Report** (the cron + reporting pipeline). Both are
on AWS Lambda + API Gateway, fronting a MySQL backend, with S3/EventBridge/SES
for storage and async side effects.

---

## A. Account Review Dashboard — UMA/SMA flow

### The story (30-second version)

"We have a portfolio management dashboard where ops/advisors review pending
account requests — UMA, SMA, or sponsor-level. The list page hits one endpoint
to get all pending requests. Clicking into a specific SMA account opens a detail
view that's actually three independent Lambdas composed together: account
metadata, the request itself, and per-position personalization rules (min/max
weight bands, tax budget, substitution instrument). The frontend fires those
three in parallel rather than waiting on each sequentially."

### Endpoint breakdown (what each one actually does)

| Endpoint | Returns | Notes |
|---|---|---|
| `GET /requests?resources={...}` | All pending UMA/SMA/sponsor requests | List/landing page; `resources` param scopes which service/resource is asking |
| `GET /account-detail/{accountId}` | Static account metadata: type, portfolio type, strategy, custodian, total market value | Read-mostly reference data |
| `GET /requests/{accountId}` | The specific pending request tied to that account | Drives the "what needs approval" panel |
| `GET /account-personalization-pos/{accountId}` | Per-holding customization: min/max weight, tax budget, substitution symbol | Includes `fieldEditAllowed` + `fieldLabels` metadata — **the API tells the frontend which fields are editable**, not just the data |

### The detail worth mentioning unprompted

The personalization endpoint returns `operationAllowed` (`insert`/`update`/`delete`)
and `fieldEditAllowed` per field, alongside `validationStatus`. That means the
**backend, not the frontend, owns the authorization/business-rule decisions**
about what's editable — the UI just renders what it's told. This is a good
answer if asked "how do you handle permissions in a UI with many edit states":
> "We push edit-permission logic to the API response itself, so the frontend
> stays dumb and the rules live in one place — no risk of the UI allowing an
> edit the backend will reject anyway."

That's the **Strategy/Policy pattern** applied to API design — the policy decision
(can this field be edited) is computed server-side per request, not hardcoded
in the client.

---

## B. AUM Quarterly Report — cron + reporting pipeline

### The story (30-second version)

"This is a quarterly billing report. An EventBridge cron fires at midnight UTC
on the 1st of Jan/Apr/Jul/Oct. The Lambda computes the quarter's date range,
pulls every active trading account from MySQL (joined across account, strategy,
and portfolio-spec tables), pulls each account's daily AUM for that quarter,
computes a daily average and a 3-bps annualized fee, builds a CSV, uploads it
to S3, then fires an EventBridge event that a separate mail Lambda picks up to
email the report out. There's also a paired read API that lists/downloads past
reports via presigned S3 URLs."

### Why it's built the way it is (the parts that show seniority)

1. **Cron correctness without off-by-one bugs**
   The cron fires on day 1 of the new quarter, and the code does
   `quarterEnd = yesterday`. Because the cron always fires exactly one day after
   the quarter closes, "yesterday" is *guaranteed* to be the last day of the
   just-finished quarter — no date-math edge cases needed.
   *If asked "how would you test this kind of date logic":* "I'd unit test
   `computeQuarterDates` directly with fixed `Date` inputs for all four quarter
   boundaries, plus the manual-override path (`quarterEndDate` passed in the
   event), separately from the DB/IO parts."

2. **Manual override built in**
   The same Lambda accepts a `quarterEndDate` in the event payload for backfills/
   re-runs — you don't need a second code path to regenerate a missed or
   corrected report.

3. **Derived-strategy weighting**
   Some accounts are tagged "DERIVED" (`str_type_id = 3`) and their AUM gets
   scaled by a separately-fetched sub-strategy weight before reporting — i.e.
   the report isn't a flat SUM, it's weighted per business rule. Good example of
   *"the simplest query wasn't enough — I had to layer business logic on top."*

4. **Reader replica for all reads**
   `useReaderInstance = true` — every query in this report runs against the read
   replica, so a heavy quarterly aggregation never competes with production
   write traffic on the primary.

5. **Decoupled email via EventBridge, not direct SES call**
   The report Lambda doesn't call SES itself — it emits a `Send-Mail` event;
   a separate `ModelMailService` Lambda (subscribed to that event pattern, along
   with several *other* unrelated source systems) sends it. This is the same
   mail Lambda reused by completely different report types — **one
   Observer/event consumer, many independent producers.**
   *If asked "why not just call SES directly":* "So the report-generation Lambda
   doesn't need SES IAM permissions or know about email formatting at all, and
   we get one shared, testable mail path for every report in the system instead
   of duplicating SES logic in each producer."

6. **Presigned URLs, short TTL (120s)**
   The list/download API never returns a public S3 URL — it signs one on demand
   with a 2-minute expiry. *If asked why so short:* "These are financial
   reports; we don't want a long-lived link sitting in a browser history or
   email that someone could reuse later. The frontend is expected to consume it
   immediately."

7. **S3 listing handles pagination**
   `listS3Files` loops on `ContinuationToken` — correct behavior as the report
   archive grows past 1000 objects (S3's per-page limit), not just "works in dev
   with 5 files."

8. **Validation isolated from business logic**
   `ListAumReportInputValidation.js` uses AJV with custom `errorMessage`s, kept
   completely separate from `ListAumReport.js`. Same layered pattern as Doc 1,
   Part C.

### Likely follow-up questions and how to answer them

**"What would you change about this design?"**
A solid honest answer: "Right now the three DB queries
(`fetchAccountData`, `fetchQuarterAum`, `fetchDerivedWeights`) run sequentially
even though they're independent until the join in `processor()` — I could
`Promise.all` the first and third since neither depends on the other's result,
which would shave latency on a quarterly job that's not time-critical but still
worth it if the dataset grows."

**"How do you handle a partial failure — e.g. CSV uploads but the email fails?"**
"The S3 upload and the EventBridge trigger are two separate steps with their own
try/catch and custom error types (`S3Error`, `EventBridgeTriggerError`). If the
email step fails, the report still exists in S3 and is recoverable via the list/
download API — failure in one step doesn't lose the other's work. I'd consider
adding a retry or DLQ on the EventBridge rule for the mail step specifically."

**"How would you scale this if the number of accounts 10x'd?"**
"The query layer already isolates SQL in a model file, so I'd start by checking
query plans/indexes on `t_cln_account_daily_aum` before touching Lambda memory/
timeout. If the in-memory aggregation in `processor()` became the bottleneck,
I'd consider pushing the SUM/AVG into SQL instead of JS, since it's currently
done in application code (`for` loop in `processor`)."

**"Why Lambda instead of a long-running service for a quarterly batch job?"**
"It runs 4 times a year and finishes in well under the timeout window (300s
configured) — there's no benefit to paying for an always-on server for something
this infrequent. Lambda + EventBridge cron is the natural fit."

---

## One-sentence summaries to have ready

- **Account Review Dashboard:** "A multi-Lambda composed dashboard where the
  backend owns field-level edit permissions, not just data."
- **AUM Quarterly Report:** "A cron-triggered Lambda pipeline — validate → fetch
  → compute weighted fees → CSV → S3 → EventBridge → email — fully decoupled
  from the downstream mail service."

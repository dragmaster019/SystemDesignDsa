# Doc 1 — MERN + AWS + Design Patterns (Interview-Ready)

Goal: you can explain the **whole flow** — UI click → API Gateway → Lambda →
DB/S3 → response — and name the pattern at each step.

---

## PART A — How a request actually flows (the "whole UI and flow" story)

```
Browser (React)
   |  user clicks "SMA / Account Review"
   v
React component calls API (axios/fetch)
   |  GET https://devapi.../requests/v1/pm/requests?resources=...
   v
AWS API Gateway  (HTTP API or REST API)
   |  - validates route, applies authorizer (IAM/Cognito/JWT)
   |  - throttles, applies CORS
   v
AWS Lambda (Node.js handler)
   |  handler -> input validation -> preProcessor -> processor -> response
   v
MySQL/DynamoDB (read replica) or S3
   v
Response bubbles back up through Lambda -> API Gateway -> React
   |
   v
React updates state -> re-renders dashboard
```

**Say this in the interview:** "The frontend never talks to the database directly.
Every screen is backed by one or more REST endpoints, each fronted by API Gateway,
each handled by an independent Lambda. So a dashboard page is really N parallel
API calls composed in the UI, not one big monolith endpoint."

This is exactly your real flow:
1. List page → `GET /requests?resources={...}` (all UMA/SMA/sponsor requests)
2. Click a row → navigate to detail page
3. Detail page fires 3 parallel calls: `account-detail/{id}`, `requests/{id}`,
   `account-personalization-pos/{id}` — each a separate microservice/Lambda

---

## PART B — React (what they'll actually ask)

### 1. Hooks you must know cold

```jsx
function AccountDetail({ accountId }) {
  const [account, setAccount] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    async function load() {
      const res = await fetch(`/account-detail/${accountId}`);
      const json = await res.json();
      if (!cancelled) {
        setAccount(json.data);
        setLoading(false);
      }
    }
    load();
    return () => { cancelled = true; }; // cleanup — avoid setState after unmount
  }, [accountId]);

  if (loading) return <Spinner />;
  return <AccountCard account={account} />;
}
```

Talking points:
- `useEffect` dependency array — re-runs when `accountId` changes (e.g. user clicks a different row)
- Cleanup function — prevents the classic "setState on unmounted component" bug
- `useMemo`/`useCallback` — only when you can point to an actual re-render cost, not by default

### 2. Composing a dashboard from multiple APIs (your exact case)

```jsx
function AccountReviewDashboard({ accountId }) {
  const [detail, setDetail] = useState(null);
  const [request, setRequest] = useState(null);
  const [positions, setPositions] = useState(null);

  useEffect(() => {
    Promise.all([
      fetch(`/account-detail/${accountId}`).then(r => r.json()),
      fetch(`/requests/${accountId}`).then(r => r.json()),
      fetch(`/account-personalization-pos/${accountId}`).then(r => r.json()),
    ]).then(([d, r, p]) => {
      setDetail(d.data);
      setRequest(r.data);
      setPositions(p.data);
    });
  }, [accountId]);

  return (
    <>
      <AccountSummary detail={detail} />
      <RequestPanel request={request} />
      <PositionsTable positions={positions?.data} />
    </>
  );
}
```

`Promise.all` — fire 3 independent calls in parallel instead of sequentially.
This is the single most common "explain how you'd build this page" interview question.

### 3. State management — when to reach for Redux/Context vs local state

- Local `useState` — data only one component tree needs (this account's positions)
- Context — small, rarely-changing global data (logged-in user, theme)
- Redux/Zustand — large app, many unrelated components need the same server data,
  or you need caching/optimistic updates (React Query/TanStack Query is the modern
  answer for "server state" specifically — mention it, interviewers like it)

---

## PART C — Node.js / Express (or Lambda) backend patterns

### 1. The layered handler pattern (this is literally what your own code does)

```js
export const handler = async (event, context) => {
  try {
    withRequest(event, context);                 // logging context
    const input = parseInput(event);              // 1. parse
    await validateInput(input);                    // 2. validate (AJV schema)
    const prepared = await preProcessor(input);     // 3. pre-process / enrich
    const result = await processor(prepared);       // 4. core business logic
    return packResponse('Success', 200, 'OK', null, result);
  } catch (error) {
    return packResponse('Error', error.statusCode ?? 500, error.userMessage ?? error.message, error);
  }
};
```

Why this matters to an interviewer: it's **Separation of Concerns** — validation,
business logic, and response shaping never mix in one function. Each piece is
independently testable.

### 2. Custom error classes (Chain of Responsibility / typed errors)

```js
class AppError extends Error {
  constructor({ sourceMessage, userMessage, statusCode = 500 }) {
    super(sourceMessage);
    this.type = 'CUSTOM_ERROR';
    this.userMessage = userMessage;
    this.statusCode = statusCode;
  }
}

class ValidationError extends AppError {
  constructor(args) { super({ ...args, statusCode: 400 }); }
}

class DatabaseError extends AppError {
  constructor(args) { super({ ...args, statusCode: 500 }); }
}
```

The catch block checks `error.type === 'CUSTOM_ERROR'` to decide whether to show
the safe `userMessage` to the client or a generic 500 — **never leak raw DB/SQL
errors to the frontend.**

### 3. Repository-style query builders (decouples SQL from business logic)

```js
// Model layer — only builds queries, never executes them directly tangled with logic
export const buildAccountQuery = (db, accountId) =>
  `SELECT * FROM accounts WHERE account_id = ${db.escape(accountId)}`;

// Service layer — owns execution + error handling
const fetchAccount = async (accountId) => {
  const query = buildAccountQuery(dbConnection, accountId);
  try {
    const [rows] = await dbConnection.query(query);
    return rows;
  } catch (error) {
    throw new DatabaseError({ sourceMessage: error.message, userMessage: 'Could not load account.' });
  }
};
```

This is the **Repository pattern** — if you swap MySQL for DynamoDB later, only
this file changes, not the business logic calling it.

---

## PART D — AWS services, explained the way interviewers want to hear it

| Service | One-line explanation | Why it's used |
|---|---|---|
| **API Gateway** | The front door — receives HTTP requests, runs auth, routes to Lambda | Decouples public HTTP surface from compute; handles throttling/CORS centrally |
| **Lambda** | Stateless function that runs your handler code on-demand | No server to manage; scales per-request; pay only for execution time |
| **EventBridge** | Pub/sub event bus — one service emits an event, others react | Decouples services — report Lambda doesn't know/care how email gets sent |
| **S3** | Object storage (files, CSVs, reports) | Durable, cheap; presigned URLs let the browser download privately-stored files securely |
| **SSM Parameter Store** | Centralized config/secrets store | No hardcoded env vars in code; same Lambda works across dev/staging/prod by just changing params |
| **VPC** | Private network the Lambda runs inside | Lets Lambda reach a private RDS instance that isn't exposed to the public internet |
| **SES** | Transactional email sending | Used by a downstream "mail service" Lambda triggered via EventBridge |
| **CloudWatch** (implied by `logger`) | Logs/metrics | Every `logger.info/error` call ends up here for debugging production issues |

### How API Gateway actually works, step by step (a favorite interview question)

1. Client sends `GET https://api.example.com/v1/pm/requests?...`
2. API Gateway matches the route + method against your defined resources
3. Runs the **authorizer** (e.g. `aws_iam`, or a Cognito/JWT authorizer) — rejects with 401/403 before Lambda ever runs
4. Applies **request validation** (if a model schema is attached) and **CORS** headers
5. Invokes the Lambda, passing the `event` object (query params, headers, body)
6. Lambda returns a structured response (`statusCode`, `headers`, `body`)
7. API Gateway forwards that response back to the client, applying any response mapping

**Key point to mention:** API Gateway + Lambda is **synchronous request/response**,
whereas EventBridge is **asynchronous, fire-and-forget** — that's why long-running
side effects (sending email, generating a report) go through EventBridge instead
of blocking the original HTTP response.

---

## PART E — Design patterns mapped to MERN, with a sentence to say in the interview

1. **Factory** — "I use a factory function to decide which payment/notification
   class to instantiate based on a type string, so the caller never imports
   concrete classes directly."
2. **Strategy** — "Different report types or pricing rules are interchangeable
   strategy objects passed into a shared `Checkout`/`Report` runner — adding a
   new one means adding a new class, not editing existing logic."
3. **Singleton** — "DB connections and AWS SDK clients (`S3Client`, `SESClient`)
   are created once per Lambda cold start and reused across invocations to avoid
   reconnect overhead."
4. **Observer / Pub-Sub** — "EventBridge is essentially Observer at infrastructure
   scale — the report Lambda publishes an event; the mail Lambda subscribes to it.
   Neither knows the other exists."
5. **Repository** — "Query-building is isolated in a `*Model.js` file, so SQL
   never leaks into the handler or business logic."
6. **Middleware/Chain of Responsibility** — "Express middleware (or my own
   validate → preProcessor → processor chain) lets each layer handle one concern
   and pass control to the next."
7. **Builder** — "For objects with many optional fields (e.g. constructing an
   email payload with optional cc/bcc/attachments), I build it up field by field
   instead of one giant constructor."

---

## How to use this doc in the interview

If asked "walk me through how a request flows through your system," answer in this order:
1. UI event → API call (mention `Promise.all` if multiple calls)
2. API Gateway (auth + routing)
3. Lambda layered handler (validate → preProcess → process)
4. Data layer (DB query builder / S3 / EventBridge)
5. Response shape back to UI
6. Name 1-2 design patterns you used along the way, unprompted — this is what
   separates "mid" from "senior" answers.

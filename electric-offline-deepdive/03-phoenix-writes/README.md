# 03 — Phoenix Writes: The Only Way Money Moves

## Golden rules
1. Device → `POST /api/transfers` with `Idempotency-Key`. Never INSERT into Postgres/Electric from client.
2. Validate BEFORE opening tx. Keep tx < 100ms. Never HTTP-call inside tx.
3. `SELECT ... FOR UPDATE` the debit row. `UNIQUE(idempotency_key)` is your double-charge insurance.
4. Return `201 posted` / `200 duplicate` / `422 rejected`. Device reconciles on these.

## Canonical implementation (copy into `lib/fintech/ledger.ex`)
```elixir
alias Ecto.Multi
alias Fintech.{Repo, Accounts.Account, Ledger.Entry}
import Ecto.Query

def transfer(from_id, to_id, amount, idem_key) with do
  {:ok, _} <- validate_different(from_id, to_id),
  {:ok, _} <- validate_amount(amount)
else
  err -> {:error, err}
end
|> case do
  {:error, _} = e -> e
  {:ok, _} ->
    Multi.new()
    |> Multi.run(:debit_acct, fn repo, _ ->
      # row lock — serializes concurrent debits on same account
      case repo.one(from(Account, where: [id: ^from_id], lock: "FOR UPDATE")) do
        nil -> {:error, :from_not_found}
        %{balance_paise: b} when b < amount -> {:error, :insufficient_funds}
        acct -> {:ok, acct}
      end
    end)
    |> Multi.insert(:entry, Entry.changeset(%Entry{}, %{
        from_id: from_id, to_id: to_id,
        amount_paise: amount, idempotency_key: idem_key, branch_id: "blr"}))
    |> Multi.update_all(:debit, from(a in Account, where: a.id == ^from_id),
        inc: [balance_paise: ^(-amount)])
    |> Multi.update_all(:credit, from(a in Account, where: a.id == ^to_id),
        inc: [balance_paise: ^amount])
    |> Repo.transaction(timeout: 5_000)
    |> case do
      {:error, :entry, %{errors: [idempotency_key: _]}, _} -> {:ok, :duplicate}
      other -> other
    end
end
```

## Outbox alternative (when you need events)
If downstream (SMS, webhook) must fire per transfer, write to `outbox` table INSIDE the same Multi, then a separate Oban worker publishes. Never publish before commit.

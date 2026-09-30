# 02 — Phoenix Ledger API (Own Features End-to-End)

## What client expects
Turn "user can transfer money" into `POST /api/transfers` in <1 day, with auth, validation, idempotency, tests.

## Production structure (Phoenix 1.7+)
```
lib/fintech/
  accounts.ex      # context: business logic
  ledger.ex        # Ecto.Multi transfers
  ledger/account.ex
  ledger/entry.ex
lib/fintech_web/
  controllers/transfer_controller.ex
  controllers/transfer_json.ex
```

## Key file: `lib/fintech/ledger.ex`
```elixir
defmodule Fintech.Ledger do
  import Ecto.Query
  alias Fintech.Repo
  alias Fintech.Ledger.{Account, Entry}
  alias Ecto.Multi

  # Idempotency: client sends `Idempotency-Key: <uuid>` header.
  # We store it UNIQUE in DB. Retry = return original result.
  def transfer(from_id, to_id, amount_paise, idempotency_key) do
    Multi.new()
    |> Multi.run(:checks, fn _, _ -> validate(from_id, to_id, amount_paise) end)
    |> Multi.run(:lock_from, fn repo, _ ->
      # SELECT ... FOR UPDATE — prevents double-spend race
      case repo.get(Account, from_id) |> lock_row(repo) do
        nil -> {:error, :from_not_found}
        acc when acc.balance_paise < amount_paise -> {:error, :insufficient_funds}
        acc -> {:ok, acc}
      end
    end)
    |> Multi.insert(:entry, Entry.changeset(%Entry{}, %{
      from_id: from_id, to_id: to_id,
      amount_paise: amount_paise, idempotency_key: idempotency_key
    }))
    |> Multi.update_all(:debit, from(Account, where: [id: ^from_id]),
      inc: [balance_paise: ^(-amount_paise)])
    |> Multi.update_all(:credit, from(Account, where: [id: ^to_id]),
      inc: [balance_paise: ^amount_paise])
    |> Repo.transaction()
  end
end
```

See `transfer_controller.ex` for controller + idempotency header handling.

## Run (after `mix phx.new fintech`)
```bash
mix ecto.create && mix ecto.migrate
mix test test/fintech/ledger_test.exs
mix phx.server
curl -X POST localhost:4000/api/transfers \
  -H "Idempotency-Key: abc-123" -H "Content-Type: application/json" \
  -d '{"from":"a","to":"b","amount_paise":3000}'
```

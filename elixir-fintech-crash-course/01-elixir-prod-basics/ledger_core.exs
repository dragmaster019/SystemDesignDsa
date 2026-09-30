# Double-entry core in pure Elixir (no deps). Run: elixir ledger_core.exs
# Lesson: money is integers, every transfer = 2 entries, with-chain for validation.

defmodule LedgerCore do
  @moduledoc "Minimal production-style ledger logic."

  defstruct accounts: %{}, entries: []

  # amounts in paise (integer). Never float.
  def new, do: %__MODULE__{}

  def open_account(%__MODULE__{accounts: accs} = l, id, opening_paise) when opening_paise >= 0 do
    if Map.has_key?(accs, id), do: {:error, :account_exists}, else: {:ok, %{l | accounts: Map.put(accs, id, opening_paise)}}
  end

  # Production pattern: `with` pipeline -> validate -> apply
  def transfer(ledger, from, to, amount_paise, opts \\ []) do
    idempotency_key = Keyword.get(opts, :idempotency_key)

    with {:ok, amount} <- validate_amount(amount_paise),
         {:ok, _} <- validate_different(from, to),
         {:ok, balances} <- validate_funds(ledger.accounts, from, amount),
         :ok <- check_idempotency(ledger, idempotency_key) do
      new_balances =
        balances
        |> Map.update!(from, &(&1 - amount))
        |> Map.update!(to, fn bal -> (bal || 0) + amount end)

      entry = %{from: from, to: to, amount_paise: amount, key: idempotency_key, at: DateTime.utc_now()}
      {:ok, %{ledger | accounts: new_balances, entries: [entry | ledger.entries]}}
    end
  end

  defp validate_amount(a) when is_integer(a) and a > 0, do: {:ok, a}
  defp validate_amount(_), do: {:error, :invalid_amount}

  defp validate_different(a, a), do: {:error, :same_account}
  defp validate_different(_, _), do: {:ok, :ok}

  defp validate_funds(accounts, from, amount) do
    case Map.fetch(accounts, from) do
      :error -> {:error, :from_not_found}
      {:ok, bal} when bal < amount -> {:error, :insufficient_funds}
      {:ok, _} -> {:ok, accounts}
    end
  end

  defp check_idempotency(_, nil), do: :ok
  defp check_idempotency(%{entries: es}, key) do
    if Enum.any?(es, &(&1.key == key)), do: {:error, :duplicate_idempotency_key}, else: :ok
  end

  # Trial balance: sum must be conserved (no money created/destroyed)
  def total(%{accounts: a}), do: Enum.reduce(a, 0, fn {_, v}, s -> s + v end)
end

defmodule Demo do
  def run do
    {:ok, l} = LedgerCore.new() |> LedgerCore.open_account("alice", 100_00) |> elem(1) |> LedgerCore.open_account("bob", 50_00)
    IO.puts("total before: #{LedgerCore.total(l)} paise")

    {:ok, l2} = LedgerCore.transfer(l, "alice", "bob", 30_00, idempotency_key: "pay-1")
    IO.inspect(l2.accounts, label: "after pay-1")

    # duplicate retry with same key -> rejected (safe retry, critical for payments)
    IO.inspect(LedgerCore.transfer(l2, "alice", "bob", 30_00, idempotency_key: "pay-1"), label: "retry same key")

    # overdraft -> rejected
    IO.inspect(LedgerCore.transfer(l2, "alice", "bob", 999_999_00), label: "overdraft")

    IO.puts("total after: #{LedgerCore.total(l2)} paise (must equal before)")
  end
end

Demo.run()

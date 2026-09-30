# Run: elixir conflict_sim.exs — two offline branches, one account, only one wins
defmodule Server do
  # Single source of truth, like Postgres row + FOR UPDATE
  def new(balance), do: %{balance: balance, log: []}

  # Returns {:posted | :duplicate | :rejected, new_state}
  def submit(state, %{key: k, amount: amt, label: label}) do
    cond do
      Enum.any?(state.log, &(&1.key == k)) -> {:duplicate, state}
      amt <= 0 -> {:rejected, state}
      state.balance < amt ->
        IO.puts("  ✗ #{label} REJECTED (need #{amt}, have #{state.balance})")
        {:rejected, state}
      true ->
        ns = %{state | balance: state.balance - amt, log: [%{key: k, amount: amt, label: label} | state.log]}
        IO.puts("  ✓ #{label} POSTED #{amt}, remaining #{ns.balance}")
        {:posted, ns}
    end
  end
end

IO.puts("== Offline: both branches queue while disconnected ==")
branch_a = %{key: "uuid-A", amount: 8000, label: "A->B 8000 (branch-A offline)"}
branch_b = %{key: "uuid-B", amount: 7000, label: "A->C 7000 (branch-B offline)"}
IO.puts("  queued: #{branch_a.label}")
IO.puts("  queued: #{branch_b.label}")

IO.puts("\n== Reconnect: branch-A flushes first ==")
s = Server.new(10_000)
{_, s} = Server.submit(s, branch_a)

IO.puts("\n== Branch-B flushes second (stale balance!) ==")
{result, s} = Server.submit(s, branch_b)
IO.puts("  result: #{result} (client must show FAILED, refresh from shape)")

IO.puts("\n== Retry with same key is safe (network duplicate) ==")
{r2, _} = Server.submit(s, branch_a)
IO.puts("  result: #{r2} (no double charge)")

IO.puts("\nfinal balance: #{s.balance} (10000 - 8000 = 2000, money conserved)")

# Run: elixir reconciliation.exs — outbox flush handling 201/200/422 like client_offline.js
defmodule FakeServer do
  def new(balance, seen \\ MapSet.new()), do: %{balance: balance, seen: seen}
  # mimics Phoenix API status codes
  def post(s, %{key: k, amount: a}) do
    cond do
      MapSet.member?(s.seen, k) -> {200, s}
      s.balance < a -> {422, s}
      true -> {201, %{s | balance: s.balance - a, seen: MapSet.put(s.seen, k)}}
    end
  end
end

defmodule Outbox do
  def flush(server, queue) do
    Enum.reduce(queue, {server, []}, fn item, {s, failed} ->
      {status, ns} = FakeServer.post(s, item)
      case status do
        201 -> IO.puts("  [201] #{item.key} confirmed, remove from outbox"); {ns, failed}
        200 -> IO.puts("  [200] #{item.key} duplicate, remove (already posted)"); {ns, failed}
        422 -> IO.puts("  [422] #{item.key} FAILED (insufficient) — keep as failed, refresh UI"); {ns, [item | failed]}
      end
    end)
  end
end

queue = [
  %{key: "k1", amount: 8000},  # will post
  %{key: "k1", amount: 8000},  # duplicate retry (e.g. user double-tapped) — dup key
  %{key: "k2", amount: 7000}   # overspend after k1 — must 422
]

IO.puts("flushing 3 queued intents against balance 10000:")
{server, failed} = Outbox.flush(FakeServer.new(10_000), queue)
IO.puts("server balance: #{server.balance}, failed count: #{length(failed)} (expect 1)")

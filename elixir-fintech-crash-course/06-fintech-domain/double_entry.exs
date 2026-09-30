# Run: elixir double_entry.exs — append-only ledger, refunds as reversing entries
defmodule Bank do
  defstruct entries: []
  def new, do: %__MODULE__{}

  def post(%__MODULE__{entries: es} = b, from, to, amt, meta \\ %{}) do
    entry = Map.merge(%{from: from, to: to, amount: amt, id: length(es) + 1}, meta)
    {:ok, %{b | entries: [entry | es]}}
  end

  def refund(b, entry_id) do
    orig = Enum.find(b.entries, &(&1.id == entry_id))
    post(b, orig.to, orig.from, orig.amount, %{reverses: entry_id})
  end

  def balance(%{entries: es}, acct) do
    Enum.reduce(es, 0, fn e, s ->
      s + (if e.to == acct, do: e.amount, else: 0) - (if e.from == acct, do: e.amount, else: 0)
    end)
  end
end

b = Bank.new()
{:ok, b} = Bank.post(b, "alice", "bob", 5000)
{:ok, b} = Bank.post(b, "bob", "lender", 2000)
IO.puts("bob balance: #{Bank.balance(b, "bob")} (expect 3000)")
{:ok, b} = Bank.refund(b, 1)  # refund first payment, not delete
IO.puts("after refund — alice: #{Bank.balance(b, "alice")}, bob: #{Bank.balance(b, "bob")}")
IO.puts("entries: #{length(b.entries)} (append-only, audit trail intact)")

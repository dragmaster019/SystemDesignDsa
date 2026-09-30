# Run: elixir transfer_server.exs — 1000 concurrent transfers, balance always consistent
defmodule TransferServer do
  use GenServer

  def start_link(init), do: GenServer.start_link(__MODULE__, init, name: __MODULE__)
  def init(balances), do: {:ok, balances}
  def transfer(from, to, amt), do: GenServer.call(__MODULE__, {:transfer, from, to, amt})
  def balances, do: GenServer.call(__MODULE__, :balances)

  # All transfers serialize here — like SELECT ... FOR UPDATE in Postgres
  def handle_call({:transfer, from, to, amt}, _from, bal) do
    cond do
      amt <= 0 -> {:reply, {:error, :invalid}, bal}
      Map.get(bal, from, 0) < amt -> {:reply, {:error, :insufficient}, bal}
      true ->
        nb = bal |> Map.update!(from, &(&1 - amt)) |> Map.update(to, amt, &(&1 + amt))
        {:reply, :ok, nb}
    end
  end
  def handle_call(:balances, _, bal), do: {:reply, bal, bal}
end

{:ok, _} = TransferServer.start_link(%{"alice" => 100_000, "bob" => 100_000})

tasks = for _ <- 1..1000 do
  Task.async(fn ->
    # random direction, simulating concurrent branch agents
    if :rand.uniform(2) == 1, do: TransferServer.transfer("alice", "bob", 10),
    else: TransferServer.transfer("bob", "alice", 10)
  end)
end
Enum.each(tasks, &Task.await/1)

final = TransferServer.balances()
IO.inspect(final, label: "final balances")
IO.puts("total: #{final["alice"] + final["bob"]} (must be 200000 — no money lost/created)")

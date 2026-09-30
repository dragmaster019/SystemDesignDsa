# 04 - Functions + Modules (Module = Java class). Run: elixir 04_functions_modules.exs

# --- anonymous fn (lambda) ---
add = fn a, b -> a + b end
IO.puts("add(2,3)=#{add.(2, 3)}")  # note the DOT: add.(...)

# --- named fn inside module (like static methods) ---
defmodule Math do
  def add(a, b), do: a + b                    # one-liner
  def sub(a, b) do                             # block
    a - b
  end
  defp secret(x), do: x * 1000                # private (like Java private)
  def pub(x), do: secret(x) + 1               # public calls private

  # multiple clauses = if-else by pattern (use this!)
  def fee(amount) when amount < 1000, do: 5
  def fee(amount) when amount >= 1000, do: 10

  # default arg \\
  def greet(name \\ "guest"), do: "hi #{name}"
end

IO.puts("Math.add=#{Math.add(5, 6)}")
IO.puts("Math.sub=#{Math.sub(9, 4)}")
IO.puts("Math.pub=#{Math.pub(2)}")
IO.puts("fee 500=#{Math.fee(500)} fee 5000=#{Math.fee(5000)}")
IO.puts(Math.greet() <> " / " <> Math.greet("bob"))

# --- pipe into functions ---
IO.puts("--- pipe ---")
result = [3, 1, 2] |> Enum.sort() |> Enum.map(fn x -> x * 10 end)
IO.inspect(result, label: "sorted*10")

# --- struct (typed map, like Java class fields) ---
defmodule User do
  defstruct [:id, balance: 0]   # balance defaults 0
  def new(id, bal), do: %User{id: id, balance: bal}
  def debit(%User{balance: b} = u, amt) when b >= amt, do: {:ok, %{u | balance: b - amt}}
  def debit(_, _), do: {:error, :insufficient_funds}
end

u = User.new("alice", 5000)
IO.inspect(u, label: "user struct")
IO.inspect(User.debit(u, 1000), label: "debit 1000")
IO.inspect(User.debit(u, 99999), label: "debit too much")

# --- case / cond / if (branching) ---
 bal = 500
msg = case bal do
  b when b <= 0 -> "empty"
  b when b < 1000 -> "low"
  _ -> "ok"
end
IO.puts("balance msg=#{msg}")
if bal > 0, do: IO.puts("positive"), else: IO.puts("zero/neg")

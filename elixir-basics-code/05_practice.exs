# 05 - Practice: write your own below. Run: elixir 05_practice.exs
# Uncomment each task and implement.

IO.puts("TASK 1: make list [1,2,3,4,5], print only even numbers")
# your code here:
# nums = [1,2,3,4,5]
# ...

IO.puts("\nTASK 2: map %{id: \"bob\", balance: 1000}, debit 300, print new map")
# ...

IO.puts("\nTASK 3: list of users, find total balance with Enum.reduce")
# users = [%{id: \"a\", bal: 100}, %{id: \"b\", bal: 250}]
# ...

IO.puts("\nTASK 4 (fintech): fn transfer(from_map, to_map, amt) -> {:ok, {new_from, new_to}} or {:error, :insufficient}")
# defmodule ... (try! solution in 01-elixir-prod-basics/ledger_core.exs if stuck)

# --- SOLUTIONS (comment out tasks above and uncomment to check) ---
# nums = [1,2,3,4,5]
# IO.inspect(Enum.filter(nums, fn n -> rem(n,2)==0 end), label: "evens")
#
# user = %{id: "bob", balance: 1000}
# IO.inspect(%{user | balance: user.balance - 300}, label: "after debit")
#
# users = [%{id: "a", bal: 100}, %{id: "b", bal: 250}]
# IO.puts("total=#{Enum.reduce(users, 0, fn u, acc -> acc + u.bal end)}")

#lets do it



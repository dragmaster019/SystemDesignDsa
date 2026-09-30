# 03 - Looping + accessing everything. Run: elixir 03_loops_access.exs
# Elixir has NO classic for(i=0;i<n;i++). Use Enum + for-comprehension.

nums = [10, 20, 30, 40]
user = %{id: "alice", balance: 5000}
users = [%{id: "a", bal: 100}, %{id: "b", bal: 200}, %{id: "c", bal: 300}]

IO.puts("--- Enum.each (just do something, returns :ok) ---")
Enum.each(nums, fn n -> IO.puts("num=#{n}") end)

IO.puts("--- Enum.map (transform, returns new list) ---")
doubled = Enum.map(nums, fn n -> n * 2 end)
IO.inspect(doubled, label: "doubled")

IO.puts("--- Enum.filter + reduce ---")
big = Enum.filter(nums, fn n -> n > 15 end)
IO.inspect(big, label: ">15")
sum = Enum.reduce(nums, 0, fn n, acc -> n + acc end)
IO.puts("sum=#{sum}")

IO.puts("--- for-comprehension (like for loop, returns list) ---")
squares = for n <- nums, do: n * n
IO.inspect(squares, label: "squares")

# for with filter
evens = for n <- [1, 2, 3, 4, 5, 6], rem(n, 2) == 0, do: n
IO.inspect(evens, label: "evens")

IO.puts("--- loop over list with index ---")
Enum.with_index(nums) |> Enum.each(fn {val, idx} -> IO.puts("idx #{idx} = #{val}") end)

IO.puts("--- loop over map ---")
Enum.each(user, fn {k, v} -> IO.puts("#{k} => #{v}") end)

IO.puts("--- loop over list of maps (most common in fintech) ---")
Enum.each(users, fn u -> IO.puts("#{u.id} has #{u.bal}") end)
total = Enum.reduce(users, 0, fn u, acc -> acc + u.bal end)
IO.puts("total bal=#{total}")

IO.puts("--- access tuple list ---")
rows = [{:ok, 100}, {:error, :no_funds}, {:ok, 200}]
for {status, val} <- rows, status == :ok, do: IO.puts("posted #{val}")

IO.puts("--- while-like via recursion (counts down) ---")
defmodule Counter do
  def down(0), do: IO.puts("done")
  def down(n) do
    IO.puts("n=#{n}")
    down(n - 1)
  end
end
Counter.down(3)

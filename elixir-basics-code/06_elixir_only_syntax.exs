# 06 - Elixir-only syntax (not in Java). Run: elixir 06_elixir_only_syntax.exs

IO.puts("--- 1. |> PIPE: pass left as first arg of right ---")
# Java: upcase(trim(s))  ->  Elixir: s |> trim() |> upcase()
IO.puts(("  alice  " |> String.trim() |> String.upcase()))
# [3,1,2] |> sort |> double:
IO.inspect([3, 1, 2] |> Enum.sort() |> Enum.map(fn x -> x * 10 end), label: "pipe result")

IO.puts("\n--- 2. & CAPTURE: make fn without writing fn ---")
# &1 = arg1, &2 = arg2, &3 = arg3
sum = &(&1 + &2)          # same as: fn a, b -> a + b end
IO.puts("sum.(2,3)=#{sum.(2, 3)}")
double = &(&1 * 2)        # same as: fn x -> x * 2 end
IO.inspect(Enum.map([1, 2, 3], double), label: "double via &")
# capture a named fn: &Module.fun/arity
up = &String.upcase/1     # fn s -> String.upcase(s) end
IO.puts(up.("bob"))

IO.puts("\n--- 3. .() DOT-CALL: call fn stored in variable ---")
add = fn a, b -> a + b end
IO.puts("add.(2,3)=#{add.(2, 3)}")
# NO dot for module fn: String.upcase("a"), Math.add(1,2) use plain .
# DOT only when fn is in a variable.

IO.puts("\n--- 4. <> CONCAT strings (Java uses +) ---")
IO.puts("hi " <> "bob" <> "!")

IO.puts("\n--- 5. | CONS: split list into head | tail ---")
[head | tail] = [10, 20, 30]
IO.puts("head=#{head} tail=#{inspect(tail)}")
IO.inspect([5 | [10, 20]], label: "prepend with |")

IO.puts("\n--- 6. = is MATCH, not assign ---")
{:ok, x} = {:ok, 42}      # checks shape, binds x=42
IO.puts("x=#{x}")
# {:ok, y} = {:error, :bad}  # would raise MatchError (uncomment to see crash)

IO.puts("\n--- 7. ^ PIN: use old value in match, don't rebind ---")
y = 10
{^y, z} = {10, 99}        # ^y means 'must equal 10', z binds 99
IO.puts("z=#{z}")
# {^y, _} = {20, 1}  # would raise, 20 != 10

IO.puts("\n--- 8. -> CLAUSE separator (in fn/case) ---")
msg = case {:ok, 5} do
  {:ok, n} -> "got #{n}"       # pattern -> result
  {:error, _} -> "fail"
end
IO.puts(msg)

IO.puts("\n--- 9. <- GENERATOR (in for/with), not comparison ---")
IO.inspect((for n <- [1, 2, 3, 4], do: n * 2), label: "for n <- list")
# with + <- : run steps, stop on first non-match
IO.inspect((with {:ok, a} <- {:ok, 10},
                 {:ok, b} <- {:ok, 20} do
  a + b
end), label: "with result")

IO.puts("\n--- 10. _ UNDERSCORE: ignore value ---")
{_, only_second} = {:ignored, 7}
IO.puts("only_second=#{only_second}")
Enum.each([1, 2], fn _ -> IO.puts("don't care value") end)

IO.puts("\n--- 11. %{map | key: v} UPDATE (only existing keys) ---")
m = %{a: 1, b: 2}
IO.inspect(%{m | b: 99}, label: "update b")

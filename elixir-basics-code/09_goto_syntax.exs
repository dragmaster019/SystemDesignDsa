# 09 - Go-to syntax: EVERYTHING with do/end vs do:. Run: elixir 09_goto_syntax.exs
# THE ONE RULE: `do: x` = one-liner (NO end). `do ... end` = block (NEEDS end).

IO.puts("--- 1. fn (anonymous, needs end) ---")
f = fn x -> x * 2 end                    # fn args -> body end
IO.puts("f.(5)=#{f.(5)}")

IO.puts("\n--- 2. def: block vs one-liner ---")
defmodule D do
  def hi_block(name) do                   # block: do ... end
    "hi #{name}"
  end
  def hi_line(name), do: "hi #{name}"     # one-liner: comma + do: (no end)
end
IO.puts(D.hi_block("a") <> " / " <> D.hi_line("b"))

IO.puts("\n--- 3. if (needs end) ---")
x = 10
if x > 5 do
  IO.puts("big")
else
  IO.puts("small")
end

IO.puts("\n--- 4. case (needs end, -> per clause) ---")
case {:ok, 5} do
  {:ok, n} -> IO.puts("got #{n}")         # pattern -> body
  {:error, _} -> IO.puts("fail")
end

IO.puts("\n--- 5. cond (needs end, condition -> body) ---")
n = 75
cond do
  n >= 90 -> IO.puts("A")
  n >= 75 -> IO.puts("B")                 # this runs
  true -> IO.puts("F")                    # default, like else
end

IO.puts("\n--- 6. for loop: block vs one-liner ---")
for n <- [1, 2, 3] do                     # block version
  IO.puts("n=#{n}")
end
evens = for n <- [1, 2, 3, 4], rem(n, 2) == 0, do: n  # one-liner with filter
IO.inspect(evens, label: "evens")

IO.puts("\n--- 7. with (needs end, <- steps) ---")
with {:ok, a} <- {:ok, 10},               # <- = step that must match
     {:ok, b} <- {:ok, 20} do
  IO.puts("sum=#{a + b}")
else
  {:error, r} -> IO.puts("fail #{r}")     # runs if any <- misses
end

IO.puts("\n--- 8. Enum (no do at all, fn inside) ---")
Enum.each([1, 2], fn y -> IO.puts("y=#{y}") end)

IO.puts("\n--- 9. CHEAT TABLE ---")
IO.puts("fn x -> ... end        | anonymous fn, ALWAYS end")
IO.puts("def f do ... end       | block fn, end")
IO.puts("def f, do: x           | one-liner, NO end (note comma)")
IO.puts("if c do ... else end   | branch, end")
IO.puts("case v do a -> b end   | match branch, end, -> per arm")
IO.puts("cond do c -> b end     | multi-if, end, true-> default")
IO.puts("for a <- l do ... end  | loop block, end")
IO.puts("for a <- l, f, do: b   | loop one-liner, NO end")
IO.puts("with a <- v do ... end | chain steps, end")
IO.puts("-> branch  |  <- step/filter  |  |> pipe  |  => old map key")

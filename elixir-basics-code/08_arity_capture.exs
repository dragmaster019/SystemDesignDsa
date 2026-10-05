# 08 - Arity, /n, call vs capture. Run: elixir 08_arity_capture.exs

# LESSON 1: arity = number of inputs. Same name + different count = different fns.
defmodule Calc do
  def greet(), do: "hi stranger"             # greet/0
  def greet(name), do: "hi #{name}"          # greet/1
  def greet(first, last), do: "hi #{first} #{last}"  # greet/2
  def double(x), do: x * 2                   # double/1
  def add(x, y), do: x + y                   # add/2
end

IO.puts("--- call directly (no & no /n, just parens) ---")
IO.puts(Calc.greet())          # arity 0 auto-picked
IO.puts(Calc.greet("bob"))     # arity 1 auto-picked
IO.puts(Calc.greet("a", "b"))  # arity 2 auto-picked

IO.puts("\n--- capture with & + /n (don't run, hand the recipe) ---")
f0 = &Calc.greet/0   # the 0-input version
f1 = &Calc.greet/1   # the 1-input version
IO.puts(f0.())       # call later with dot
IO.puts(f1.("ann"))

IO.puts("\n--- why capture? Enum.map calls it per element ---")
IO.inspect(Enum.map([1, 2, 3], &Calc.double/1), label: "[1,2,3] doubled")
# map does: double(1)->2, double(2)->4, double(3)->6. /1 = 1 input per call.

IO.puts("\n--- same without shortcut (long form) ---")
IO.inspect(Enum.map([1, 2, 3], fn x -> Calc.double(x) end), label: "via fn")

IO.puts("\n--- /2 needs 2 inputs, so map (gives 1) crashes ---")
# Uncomment to see crash:
# IO.inspect(Enum.map([1, 2, 3], &Calc.add/2))
# Fix: call /2 directly with 2 args:
IO.puts("add(2,3)=#{Calc.add(2, 3)}")
IO.inspect(Integer.parse("101", 2), label: "parse binary 101")

IO.puts("\nRULE: call with (), refer with /n, pass around with & + /n.")

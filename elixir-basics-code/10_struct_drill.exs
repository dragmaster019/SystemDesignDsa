# 10 - Struct drill: map -> struct in 5 tiny steps. Run: elixir 10_struct_drill.exs
# NOTE: structs used inside Demo.run() because a script can't touch a struct
# at top level in the same file that defines it.

defmodule User do
  defstruct [:name, age: 0]               # ONLY :name and :age allowed, age defaults 0
end

defmodule Accounts do
  def old_enough?(%User{age: a}) when a >= 18, do: true   # arg MUST be User, grab age
  def old_enough?(%User{}), do: false                    # User but young
end

defmodule Demo do
  def run do
    IO.puts("STEP 1: plain map (any keys allowed)")
    m = %{name: "bob", age: 24}
    IO.inspect(m, label: "map")
    IO.puts("read: #{m.name}")
    m2 = %{m | age: 25}                   # update existing key
    IO.inspect(m2, label: "map age 25")
    IO.inspect(Map.put(m2, :city, "blr"), label: "map + new key city (allowed)")

    IO.puts("\nSTEP 2: struct = map with NAME + FIXED keys")
    u = %User{name: "bob", age: 24}       # %Name{...} makes it
    IO.inspect(u, label: "struct (note __struct__ tag)")
    IO.puts("read same as map: #{u.name}, #{u.age}")

    IO.puts("\nSTEP 3: update struct (same | syntax, existing keys only)")
    u2 = %{u | age: 25}
    IO.inspect(u2, label: "struct age 25")
    # %{u | city: "blr"}  # CRASH: city not in defstruct (KeyError). Try it!
    u3 = Map.put(u2, :name, "bobby")
    IO.inspect(u3, label: "rename via Map.put")

    IO.puts("\nSTEP 4: pattern-match struct (pull pieces out)")
    %User{name: n, age: a} = u3           # shape must be %User{}, holes n/a fill in
    IO.puts("n=#{n} a=#{a}")

    IO.puts("\nSTEP 5: struct in function args (= Java type + extract in one)")
    IO.puts("24? #{Accounts.old_enough?(%User{name: "b", age: 24})}")
    IO.puts("10? #{Accounts.old_enough?(%User{name: "k", age: 10})}")

    IO.puts("\nRULES: %Name{} makes it. Dot reads it. %{x | k: v} copies it. %Name{} in args guards it.")
  end
end

Demo.run()

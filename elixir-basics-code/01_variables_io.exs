# 01 - Variables + Input/Output. Run: elixir 01_variables_io.exs
# Java: int x = 10;  ->  Elixir: x = 10 (rebind, not mutate)

# --- variables ---
x = 10
IO.puts("x = #{x}")

x = x + 5        # rebind: old 10 gone, x is now 15
IO.puts("x after +5 = #{x}")

name = "alice"   # string (binary)
age = 25         # integer
price = 99.5     # float (don't use for money!)
ok = true        # boolean
nothing = nil    # null in Java

IO.puts("name=#{name} age=#{age} price=#{price} ok=#{ok} nothing=#{inspect(nothing)}")

# --- constants by convention (UPPERCASE, still rebindable so don't rely) ---
@pi = 3.14  # NOTE: @ only works inside modules. Here just use normal var:
pi = 3.14
IO.puts("pi = #{pi}")

# --- output ---
IO.puts("hello")              # prints with newline, like System.out.println
IO.inspect([1, 2, 3], label: "my list")  # debug print with label, use everywhere

# --- input ---
# Uncomment to try interactive input:
# input = IO.gets("type your name: ") |> String.trim()
# IO.puts("hi #{input}")

# --- string interpolation + concat ---
first = "sar"
last = "thak"
IO.puts("#{first} #{last}")       # interpolation, like f-string
IO.puts(first <> " " <> last)     # <> joins strings (like + in Java)

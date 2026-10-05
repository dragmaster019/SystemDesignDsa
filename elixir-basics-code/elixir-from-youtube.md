# Elixir Learning Guide (from the crash course)

**How to use this:** Each section has **Why** (the reasoning), **Code** (what it looks like), and **Practice** (you write it, no solutions given). When you finish an exercise, paste your code to me and I'll bug-hunt it.

Setup for practice: make a project once with `mix new playground`, and do every exercise in `lib/playground.ex`. Run with `mix run`.

---

## 1. Why Elixir

- Built for **scalable** and **fault-tolerant** systems. It runs on the Erlang VM (BEAM), which was designed for telecom systems that must not go down.
- **Functional**: you transform data by passing values through functions, rather than mutating objects.
- **Elixir** = language. **Phoenix** = web framework built on it. Learn Elixir first, Phoenix second.

**Why this matters to you:** the core idea behind everything below is that failure is expected, so the language gives you *supervisors* that restart crashed parts instead of trying to prevent every crash.

---

## 2. Three ways to run Elixir

| Way | Command | Use when |
|---|---|---|
| Interactive shell | `iex` | Trying one-liners |
| Single script | `elixir intro.exs` | Small experiments |
| Mix project | `mix run` | Real work |

**Why three?** `.exs` files are *scripts* (interpreted, run top to bottom). `.ex` files are *compiled* code that lives in projects. Real apps are compiled, so projects use `.ex`.

```elixir
# intro.exs
IO.puts("hello world from Elixir")
```

```bash
iex                     # exit with Ctrl+C twice
elixir intro.exs
```

**Practice**
1. In `iex`, combine two strings using `<>`.
2. Write `intro.exs` that prints your name, and run it.

---

## 3. Mix: the build tool

**Why:** a project needs structure (code, tests, dependencies). Mix generates it and runs it, like `npm` + a scaffolder for Node.

```bash
mix new example      # creates the project
mix compile
mix test
mix run
iex -S mix           # shell with your project loaded
mix run -e "Example.hello()"
```

Folder map:
- `lib/` your code
- `test/` tests
- `mix.exs` project config + dependencies
- `_build/`, `deps/` generated, ignore them

### Module and function basics

```elixir
defmodule Example do
  def hello do
    :world
  end
end
```

- `defmodule` groups code (like a namespace).
- `def` defines a function. `do ... end` are the block delimiters.
- The **last expression is the return value**. There is no `return` keyword.

### Return vs print (a common beginner trap)

- In `iex`, return values are shown automatically.
- With `mix run -e`, they are **not** shown. You must print with `IO.puts`.

```elixir
def hello do
  IO.puts(:world)
end
```

**Practice**
1. Create a project, add a function `greet/1` that returns a string. Call it in `iex -S mix`.
2. Run it with `mix run -e`. Notice nothing prints. Fix it so it does.

---

## 4. The compile-time gotcha (read carefully)

**Why this is the most important section of setup:** code placed directly in a module body (outside any `def`) runs **when the module compiles, not when you run the app**. So:

```elixir
defmodule Example do
  IO.puts("hi")      # runs at compile time only
end
```

`mix run` prints "hi" the first time, then never again, because nothing recompiles until you change the file.

### The fix: define an application entry point

**Step 1:** in `mix.exs`, inside `def application do`:

```elixir
def application do
  [
    extra_applications: [:logger],
    mod: {Example, []}      # {module, args passed to start}
  ]
end
```

**Step 2:** in your module:

```elixir
defmodule Example do
  use Application

  def start(_type, _args) do
    main()
    Supervisor.start_link([], strategy: :one_for_one)
  end

  def main do
    IO.puts("running")
  end
end
```

Explained piece by piece:
- `mod: {Example, []}`: "when the app starts, call `Example.start/2`". `[]` is the args.
- `_type, _args`: the leading underscore means "I know this exists, I'm not using it". It silences unused-variable warnings.
- `Supervisor.start_link([], strategy: :one_for_one)`: a **supervisor** watches child processes and restarts them. The `[]` is the list of children (none yet). `:one_for_one` means if one child crashes, only that one is restarted. `start/2` must return a supervision tree result, so this line is needed for the app to start correctly.

Now `mix run` calls your code every time.

**Practice**
1. Set this up from scratch in a fresh project without looking back at it.
2. Put a `IO.puts` directly in the module body and another in `main`. Run `mix run` three times. Predict what you'll see first, then check.

---

## 5. Dependencies with Hex

**Why:** don't reinvent libraries. Hex is Elixir's package manager (npm / pip equivalent).

1. Find the package on hex.pm (check downloads to be sure it's legit).
2. Add to `mix.exs`:

```elixir
defp deps do
  [
    {:elixir_uuid, "~> 1.2"}
  ]
end
```

3. Install: `mix deps.get` (editing the list does not install anything by itself).
4. Use it:

```elixir
IO.puts(UUID.uuid4())
```

The video uses a UUID package. Check hex.pm for the current package name and version before copying; the module you call is `UUID`.

**Practice:** add the package, generate 3 UUIDs, print them.

---

## 6. Variables, rebinding, constants

**Why:** Elixir data is immutable. `x = 5` does not change a box, it **binds** the name `x` to a value. Writing `x = 10` later binds the name to a new value.

```elixir
x = 5
x = 10
IO.puts(x)   # 10
```

If you rebind without using the first value, the compiler warns "unused variable". It's a hint you wrote something pointless.

### "const": module attributes

```elixir
defmodule Example do
  @x 5

  def main do
    IO.puts(@x)
  end
end
```

`@x` is set at compile time for the whole module, which is the closest thing to a constant.

**Practice**
1. Bind a variable three times, trigger the unused warning, then fix it.
2. Make a module attribute `@tax_rate` and use it in a function that computes a price with tax.

---

## 7. Atoms vs strings

**Why two things?** An **atom** (`:hello`) is a constant whose name is its value. It is stored once in memory, so comparing atoms is a cheap pointer comparison. Strings are compared character by character.

- Use **atoms** for fixed labels you write in code: `:gold`, `:ok`, `:error`.
- Use **strings** for dynamic data: user input, names.
- Multi-word atom: `:"hello world"`.

```elixir
IO.puts(:hello)        # hello
```

**Practice:** list 5 values in an app you imagine (order status, user role, name, email, country). Decide atom or string for each, and write one line of reasoning each.

---

## 8. Conditionals: `if` and `case`

```elixir
status = Enum.random(["gold", "silver", "bronze"])
name = "Caleb"

if status === "gold" do
  IO.puts("Welcome to the fancy lounge #{name}")
else
  IO.puts("Get lost")
end
```

- `#{}` inserts a value into a string.
- `===` is strict equality (type matters). `==` is looser (`1 == 1.0` is true, `1 === 1.0` is false).
- If the compiler can see the condition is always the same, it warns "can never match". That's why the example used `Enum.random`.

### `case` (pattern-based branching)

```elixir
case status do
  "gold" -> IO.puts("Welcome to the fancy lounge #{name}")
  "not a member" -> IO.puts("Get subscribed")
  _ -> IO.puts("Get out")
end
```

`_` is the catch-all default. Cases are checked top to bottom, first match wins.

**Practice:** write a `case` on a membership status with at least 4 outcomes plus a default.

---

## 9. Strings, escapes, code points

```elixir
IO.puts("a\nb\tc")                 # newline and tab
IO.puts("looks like \#{x}")         # backslash stops interpolation
IO.puts(?a)                         # 97, the Unicode code point
```

**Why code points:** every character has a unique number. Hex is just a base-16 way of writing those numbers (digits 0-9 then a-f), so 256 in decimal is `0x100`.

**Practice**
1. Print a 3-line message with one `IO.puts` and `\n`.
2. Print the code point of `A`, `a`, `0`. Find the pattern.

---

## 10. Numbers

```elixir
10 + 3      # 13        (int + int = int)
10 + 3.0    # 13.0      (any float makes a float)
10 / 3      # 3.3333... (/ ALWAYS returns a float)
10 / 5      # 2.0
div(10, 3)  # 3         (integer division)
rem(10, 3)  # 1         (remainder)
1_000_000   # underscores for readability
```

- Elixir is **dynamically typed**: `a = 10` then `a = a + 5.0` is fine. Flexible, but type mistakes show up at runtime, not compile time.
- There is only one float type, and it is 64-bit (a "double" in C/C++).

### Float precision

`0.1` can't be stored exactly in binary (like 1/3 can't be exact in decimal).

```elixir
:io.format("~.20f~n", [0.1])   # 0.10000000000000000555
Float.ceil(0.1, 1)             # 0.2 (surprising!)
```

**Rule of thumb:** for money, use integers (cents) and divide at display time.

Useful modules: `Integer.gcd(25, 10)`, `Float.ceil/2`, `Integer.is_even/1` (needs `require Integer`).

**Practice**
1. Predict then check: `7 / 2`, `div(7, 2)`, `rem(7, 2)`, `7 / 7`.
2. Store a price as cents (`2050`) and print it as `$20.50` without using floats for storage.

---

## 11. Compound types

### 11.1 Dates and times

```elixir
time = Time.new!(16, 30, 0, 0)
date = Date.new!(2025, 1, 1)
dt   = DateTime.new!(date, time, "Etc/UTC")

IO.puts(inspect(dt))
IO.puts(dt.year)
```

- Use `inspect/1` to print compound values. `IO.puts` only accepts strings.
- **Bang functions (`new!`)** raise an error on bad input instead of returning `{:error, reason}`. Think "caution: may crash". The non-bang version returns a tuple you must handle.

### Mini project: countdown to New Year

```elixir
target = DateTime.new!(Date.new!(2027, 1, 1), Time.new!(0, 0, 0, 0), "Etc/UTC")
seconds = DateTime.diff(target, DateTime.utc_now())

days    = div(seconds, 86_400)
hours   = div(rem(seconds, 86_400), 3600)
minutes = div(rem(seconds, 3600), 60)
secs    = rem(seconds, 60)
```

**Why `div` + `rem`:** `div` gives whole units, `rem` gives what's left over to feed the next smaller unit. Using `/` would give fractions and lose the leftover.

**Practice:** build the countdown yourself and print: `"Time until New Year: X days Y hours Z minutes W seconds"`. Then change it to count down to your birthday.

### 11.2 Tuples (fixed-size groups)

```elixir
memberships = {:bronze, :silver, :gold}
elem(memberships, 0)             # :bronze
tuple_size(memberships)          # 3
user = {"Caleb", :gold}          # mixed types are fine

{name, membership} = user        # destructuring (pattern matching)
```

**Why destructuring works:** `=` in Elixir is a **match**, not just assignment. The left side's shape must match the right side. If it matches, names get bound.

Use tuples for small, fixed groups (like `{:ok, value}`). Appending (`Tuple.append/2`) returns a **new** tuple, so you must rebind the result.

### 11.3 Lists (variable-size)

```elixir
users = [{"Caleb", :gold}, {"Kayla", :gold}]

Enum.each(users, fn {name, membership} ->
  IO.puts("#{name} has a #{membership} membership")
end)
```

**Why use a list here:** the same code works whether there are 3 users or 300.

- `fn args -> body end` is an anonymous function.
- `Enum.each` runs it for every element and returns nothing useful (for side effects like printing).

### 11.4 Maps (key → value)

```elixir
prices = %{gold: 25, silver: 20, bronze: 15, none: 0}

prices.gold       # 25 (errors if key missing)
prices[:gold]     # 25 (nil if key missing)
prices[membership_var]
```

Use maps for lookups. Values can be anything (numbers, atoms, other maps).

### 11.5 Structs (your own types)

```elixir
defmodule Membership do
  defstruct [:type, :price]
end

defmodule User do
  defstruct [:name, :membership]
end

gold = %Membership{type: :gold, price: 25}
user = %User{name: "Caleb", membership: gold}

# pattern match on a struct
Enum.each([user], fn %User{name: name, membership: membership} ->
  IO.puts("#{name} has #{membership.type} and pays #{membership.price}")
end)
```

A struct definition is a blueprint (cookie cutter). `%User{...}` makes an actual instance.

**Which to choose?** Tuple for tiny fixed groups, list for sequences, map for lookups/flexible keys, struct when you want a named, structured, validated shape.

**Practice**
1. Build a `Book` struct (title, author, pages) and a list of 4 books. Print `"title by author (pages pages)"` for each.
2. Make a map from genre atom to a list of book titles. Look up one genre.
3. Write a function that takes a tuple `{:ok, value}` or `{:error, reason}` and prints different messages. Use `case`.

---

## 12. Mini project: guessing game

**Why this project:** it covers input, random numbers, type conversion, branching and error handling in one program.

```elixir
def main do
  correct = :rand.uniform(11) - 1      # :rand.uniform(n) gives 1..n, so this is 0..10

  guess =
    IO.gets("Guess a number between 0 and 10: ")
    |> String.trim()
    |> Integer.parse()

  case guess do
    {result, _} ->
      if result === correct do
        IO.puts("You win")
      else
        IO.puts("You lose")
      end

    :error ->
      IO.puts("Something went wrong")
  end
end
```

Key ideas:
- `|>` the **pipe operator** passes the result of the left side as the **first argument** of the right side. Read it as "then".
- `IO.gets` keeps the trailing newline, so `String.trim/1` is needed.
- `:rand.uniform` has a leading `:` because it's an **Erlang** module. Elixir runs on the Erlang VM, so you can call Erlang functions directly.
- `Integer.parse` returns `{number, rest_of_string}` or `:error`:
  - `"5"` → `{5, ""}`
  - `"5abc"` → `{5, "abc"}`
  - `"abc"` → `:error`
- Types matter with `===`: comparing the string `"8"` to the integer `8` is false. Convert first.

### Arity

`String.to_integer/1` means "function `to_integer` that takes 1 argument". Different arities are different functions (`Integer.parse/1` vs `Integer.parse/2` where the second is the base). You'll need arity again with the capture operator.

**Practice**
1. Build the game yourself.
2. Add: loop until the user guesses right, with "too high" / "too low" hints. (Hint: recursion, a function that calls itself.)
3. Add a limit of 5 tries.

---

## 13. List comprehensions

```elixir
grades = [25, 50, 75, 100]

for n <- grades do
  n + 5
end
# [30, 55, 80, 105]  <- a NEW list; the original is untouched (immutability)

for n <- grades, rem(n, 2) === 0, do: n   # filter: keeps only elements where the condition is true
```

List operations:

```elixir
list ++ [125]           # append (walks the whole list, slower for big lists)
[5 | list]              # prepend (fast; this is how lists are built internally)
```

**Why prepend is fast:** lists are linked lists. Putting something at the front is O(1), appending needs to walk to the end.

**Practice**
1. From `[1..20]` as a list, build a list of squares of only the odd numbers.
2. Given `["apple", "kiwi", "banana"]`, build a list of the lengths of words longer than 4 letters.

---

## 14. Functional programming: functions as values

**Higher-order function:** a function that takes another function as an argument.

```elixir
numbers = [1, 2, 3, 4, 5]

Enum.each(numbers, fn num -> IO.puts(num) end)

# Convert strings to ints using an existing named function
result = Enum.map(["1", "2", "3"], &String.to_integer/1)
```

- **Anonymous function:** `fn x -> ... end`.
- **Capture operator `&`:** turns a named function into a value you can pass around. You need the arity: `&String.to_integer/1`.
- Other core functions to learn: `Enum.filter`, `Enum.reduce`, `Enum.sum`, `Enum.count`, `Enum.join`.

### Writing your own functions

```elixir
def sum_and_average(numbers) do
  sum = Enum.sum(numbers)
  average = sum / Enum.count(numbers)
  {sum, average}                    # returning two values as a tuple
end

def print_numbers(numbers) do
  numbers
  |> Enum.join(" ")
  |> IO.puts()
end

def get_numbers_from_user do
  IO.puts("Enter numbers separated by spaces")

  IO.gets("")
  |> String.trim()
  |> String.split(" ")
  |> Enum.map(&String.to_integer/1)
end

def main do
  numbers = get_numbers_from_user()
  {sum, average} = sum_and_average(numbers)
  print_numbers(numbers)
  IO.puts("The sum is #{sum} and the average is #{average}")
end
```

- **Parameter** = the name in the definition. **Argument** = the value you pass in.
- `Enum.join` ↔ `String.split` are opposites.
- Tuples are a common way to return multiple values.

**Practice**
1. Write `min_max(numbers)` returning `{min, max}` without using `Enum.min_max`.
2. Write a pipeline that reads a line of words, splits it, uppercases each, and joins them with `-`.

---

## 15. Concept checklist

Tick these off only when you can write it from memory:

- [ ] Run Elixir 3 ways (iex, script, mix)
- [ ] Explain why top-level module code runs only at compile time
- [ ] Set up `mod:` + `start/2` + supervisor
- [ ] Add a dependency with Hex
- [ ] Rebind variables, use `@attributes`
- [ ] Choose between atoms and strings
- [ ] `if`, `case`, `_` default
- [ ] `/` vs `div` vs `rem`, int vs float results
- [ ] Why not to store money as floats
- [ ] `Time`, `Date`, `DateTime`, and `!` functions
- [ ] Tuples, lists, maps, structs, and when to use each
- [ ] Destructuring and pattern matching in `=`, `case` and `fn`
- [ ] The pipe operator `|>`
- [ ] `Integer.parse` and handling `{n, rest}` / `:error`
- [ ] Comprehensions with filters
- [ ] `Enum.map/each/filter/reduce`, anonymous functions, `&fun/arity`
- [ ] Write functions returning tuples

---

## 16. Capstone practice projects (no solutions, bring them to me)

1. **Membership manager:** structs for `User` and `Membership`, a list of users, a function that totals monthly revenue, and a function that groups users by membership type.
2. **Guessing game v2:** replay loop, hints, attempt limit, score tracking in a map.
3. **Countdown tool:** accept a target date from the user and print the time remaining in days/hours/minutes/seconds.
4. **Number stats CLI:** read numbers from user, print sum, average, min, max, median, and the even numbers.

---

## 17. Next steps

- Official guide at elixir-lang.org (Getting Started).
- Topics the video didn't cover but you'll need next: `with`, recursion on lists, `Enum.reduce` in depth, `GenServer`/processes, testing with ExUnit.
- Then move on to Phoenix.

---

## 18. How to get bug-hunted

Paste your solution and tell me the exercise number. I'll find bugs in it and explain *why* they're bugs, rather than handing over a finished answer.

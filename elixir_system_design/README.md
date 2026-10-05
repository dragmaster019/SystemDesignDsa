# Library Management — Java OOP → Elixir (system-design style, no API/HTTP)

Port of `RealWorldLld/LibraryManagment/` (Book, Member, Data, LibrarySystem, Main.java).
Run: `cd elixir_system_design/library_management && mix test`

## Files (mirror the Java layout)

| Java | Elixir here |
|---|---|
| `Model/Book.java` (class + getters/setters) | `lib/library_system/book.ex` (`defstruct` + `Book.new/3`) |
| `Model/Member.java` | `lib/library_system/member.ex` (`borrowed_books` list of ids) |
| `DataSet/Data.java` (static HashMaps) | `lib/library_system/data.ex` (Agent holding `%{books, members}`, supervised) |
| `Service/LibrarySystem.java` | `lib/library_system.ex` (same 5 methods, snake_case) |
| `Main.java` steps | `mix run -e '...'` demo below + `test/` |

## OOP mapping

- `class X` → `defmodule X` + `defstruct` (data) — no getters/setters, access via `book.title`
- `new Book(...)` → `Book.new(...)` returning `%Book{}`
- `static Data.books.put` (mutates global) → `Data.put_book/2` (Agent update, supervised + restartable)
- `void borrowBook` mutating objects → `borrow_book/2` returning `:ok` / `{:error, reason}`, same `IO.puts` messages
- `for (Book b : values) if available` → `Map.values |> Enum.filter |> Enum.each`
- `null` checks → `Map.fetch` + `with`

## Demo (same steps as Main.java)

```bash
cd elixir_system_design/library_management
mix run -e '
  alias LibrarySystem.{Book, Member}
  LibrarySystem.add_book(Book.new(1, "Clean Code", "Robert Martin"))
  LibrarySystem.add_book(Book.new(2, "System Design", "Alex Xu"))
  LibrarySystem.register_member(Member.new(101, "Sarthak"))
  LibrarySystem.show_available_books()
  LibrarySystem.borrow_book(101, 1)
  LibrarySystem.return_book(101, 1)
'
```

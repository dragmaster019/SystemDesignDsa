# 07 - Your LibrarySystem.java in Elixir (OOP -> structs+modules). Run: elixir 07_library_oop.exs
# JAVA: class Book { private fields + getters/setters, mutated in place }
# ELIXIR: struct (data) + module fns (return NEW data). No mutation, no null.

defmodule Book do
  defstruct [:book_id, :title, :author, available: true]  # isAvailable=true default
  def new(id, title, author), do: %Book{book_id: id, title: title, author: author}
end

defmodule Member do
  defstruct [:member_id, :name, borrowed: []]  # borrowedBooks ArrayList -> list
  def new(id, name), do: %Member{member_id: id, name: name}
end

# JAVA: static HashMap books/members mutated globally (Data.books.put)
# ELIXIR: one state map passed in, new state returned. %{books: %{id=>%Book{}}, members: %{...}}
defmodule Library do
  def new, do: %{books: %{}, members: %{}}

  # JAVA: void addBook(Book b) { Data.books.put(b.getBookId(), b); }
  def add_book(state, %Book{book_id: id} = b),
    do: %{state | books: Map.put(state.books, id, b)}

  # JAVA: void registerMember(Member m) { Data.members.put(m.getMemberId(), m); }
  def register_member(state, %Member{member_id: id} = m),
    do: %{state | members: Map.put(state.members, id, m)}

  # JAVA: void borrowBook(...) { null checks, isAvailable(), setAvailable(false), list.add }
  # ELIXIR: same logic, but returns {:ok, new_state} / {:error, reason} + no setters
  def borrow_book(state, member_id, book_id) do
    with {:ok, book} <- fetch(state.books, book_id, "Book"),
         {:ok, member} <- fetch(state.members, member_id, "Member"),
         true <- book.available do
      new_book = %{book | available: false}                    # setAvailable(false) -> new copy
      new_member = %{member | borrowed: [book_id | member.borrowed]}  # list.add -> prepend
      new_state = %{state |
        books: Map.put(state.books, book_id, new_book),
        members: Map.put(state.members, member_id, new_member)}
      IO.puts("#{member.name} borrowed: #{book.title}")
      {:ok, new_state}
    else
      {:error, _} = e -> IO.puts("Book or Member not found."); e
      false -> IO.puts("Book is not available."); {:error, :unavailable}
    end
  end

  def return_book(state, member_id, book_id) do
    with {:ok, book} <- fetch(state.books, book_id, "Book"),
         {:ok, member} <- fetch(state.members, member_id, "Member"),
         false <- book.available do
      new_book = %{book | available: true}
      new_member = %{member | borrowed: List.delete(member.borrowed, book_id)}
      new_state = %{state |
        books: Map.put(state.books, book_id, new_book),
        members: Map.put(state.members, member_id, new_member)}
      IO.puts("#{member.name} returned: #{book.title}")
      {:ok, new_state}
    else
      {:error, _} = e -> IO.puts("Book or Member not found."); e
      true -> IO.puts("Book was not borrowed."); {:error, :not_borrowed}
    end
  end

  # JAVA: for (Book book : Data.books.values()) if (book.isAvailable()) print
  def show_available(state) do
    IO.puts("Available books:")
    state.books
    |> Map.values()
    |> Enum.filter(fn b -> b.available end)
    |> Enum.each(fn b -> IO.puts("  [#{b.book_id}] #{b.title} by #{b.author}") end)
    state  # return unchanged state so calls chain
  end

  defp fetch(map, id, _label) do
    case Map.fetch(map, id) do
      :error -> {:error, :not_found}
      {:ok, v} -> {:ok, v}
    end
  end
end

# --- Main.java steps, same order ---
state = Library.new()
state = Library.add_book(state, Book.new(1, "Clean Code", "Robert Martin"))
state = Library.add_book(state, Book.new(2, "System Design", "Alex Xu"))
state = Library.add_book(state, Book.new(3, "Effective Java", "Joshua Bloch"))
state = Library.register_member(state, Member.new(101, "Sarthak"))
state = Library.register_member(state, Member.new(102, "Rahul"))

state = Library.show_available(state)
IO.puts("")
{:ok, state} = Library.borrow_book(state, 101, 1)  # Sarthak borrows Clean Code
{:error, _} = Library.borrow_book(state, 102, 1)   # Rahul tries same -> not available
IO.puts("")
state = Library.show_available(state)
IO.puts("")
{:ok, state} = Library.return_book(state, 101, 1)
IO.puts("")
_lib = Library.show_available(state)

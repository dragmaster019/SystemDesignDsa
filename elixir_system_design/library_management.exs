# Library Management in plain Elixir (no mix, no API). Run: elixir library_management.exs
# Mirrors RealWorldLld/LibraryManagment: Book.java, Member.java, Data.java, LibrarySystem.java, Main.java

defmodule Book do
  defstruct [:book_id, :title, :author, available: true]
  def new(id, title, author), do: %Book{book_id: id, title: title, author: author}
end

defmodule Member do
  defstruct [:member_id, :name, borrowed_books: []]
  def new(id, name), do: %Member{member_id: id, name: name}
end

defmodule Library do
  # Java static Data.books/members -> one state map passed along: %{books: %{id=>%Book{}}, members: %{}}
  def new, do: %{books: %{}, members: %{}}

  # Java: void addBook(Book b) { Data.books.put(b.getBookId(), b); }
  def add_book(state, %Book{book_id: id} = b),
    do: %{state | books: Map.put(state.books, id, b)}

  # Java: void registerMember(Member m) { Data.members.put(m.getMemberId(), m); }
  def register_member(state, %Member{member_id: id} = m),
    do: %{state | members: Map.put(state.members, id, m)}

  # Java: borrowBook — null checks, isAvailable, setAvailable(false), list.add
  def borrow_book(state, member_id, book_id) do
    with {:ok, book} <- Map.fetch(state.books, book_id),
         {:ok, member} <- Map.fetch(state.members, member_id) do
      if book.available do
        new_state = %{state |
          books: Map.put(state.books, book_id, %{book | available: false}),
          members: Map.put(state.members, member_id,
            %{member | borrowed_books: [book_id | member.borrowed_books]})}
        IO.puts("#{member.name} borrowed: #{book.title}")
        {:ok, new_state}
      else
        IO.puts("Book is not available.")
        {:error, :unavailable}
      end
    else
      :error ->
        IO.puts("Book or Member not found.")
        {:error, :not_found}
    end
  end

  # Java: returnBook — reverse of borrow
  def return_book(state, member_id, book_id) do
    with {:ok, book} <- Map.fetch(state.books, book_id),
         {:ok, member} <- Map.fetch(state.members, member_id) do
      if not book.available do
        new_state = %{state |
          books: Map.put(state.books, book_id, %{book | available: true}),
          members: Map.put(state.members, member_id,
            %{member | borrowed_books: List.delete(member.borrowed_books, book_id)})}
        IO.puts("#{member.name} returned: #{book.title}")
        {:ok, new_state}
      else
        IO.puts("Book was not borrowed.")
        {:error, :not_borrowed}
      end
    else
      :error ->
        IO.puts("Book or Member not found.")
        {:error, :not_found}
    end
  end

  # Java: showAvailableBooks — for (Book : values) if (isAvailable) print
  def show_available_books(state) do
    IO.puts("Available books:")
    state.books
    |> Map.values()
    |> Enum.filter(& &1.available)
    |> Enum.each(fn b -> IO.puts("  [#{b.book_id}] #{b.title} by #{b.author}") end)
    state
  end
end

# --- Main.java steps, same order ---
state = Library.new()
state = Library.add_book(state, Book.new(1, "Clean Code", "Robert Martin"))
state = Library.add_book(state, Book.new(2, "System Design", "Alex Xu"))
state = Library.add_book(state, Book.new(3, "Effective Java", "Joshua Bloch"))
state = Library.register_member(state, Member.new(101, "Sarthak"))
state = Library.register_member(state, Member.new(102, "Rahul"))
state = Library.show_available_books(state)
IO.puts("")
{:ok, state} = Library.borrow_book(state, 101, 1)
{:error, _} = Library.borrow_book(state, 102, 1)
IO.puts("")
state = Library.show_available_books(state)
IO.puts("")
{:ok, state} = Library.return_book(state, 101, 1)
IO.puts("")
_library_final = Library.show_available_books(state)

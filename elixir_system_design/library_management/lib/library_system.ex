defmodule LibrarySystem do
  @moduledoc "Mirrors Service/LibrarySystem.java method-for-method. Same prints, same logic."
  alias LibrarySystem.{Book, Member, Data}

  # Java: public void addBook(Book b) { Data.books.put(b.getBookId(), b); }
  def add_book(%Book{book_id: id} = b), do: Data.put_book(id, b)

  # Java: public void registerMember(Member m) { Data.members.put(m.getMemberId(), m); }
  def register_member(%Member{member_id: id} = m), do: Data.put_member(id, m)

  # Java: borrowBook — fetch, null check, isAvailable, setAvailable(false), list.add
  def borrow_book(member_id, book_id) do
    books = Data.get_books()
    members = Data.get_members()

    with {:ok, book} <- Map.fetch(books, book_id),
         {:ok, member} <- Map.fetch(members, member_id) do
      if book.available do
        Data.put_book(book_id, %{book | available: false})
        Data.put_member(member_id, %{member | borrowed_books: [book_id | member.borrowed_books]})
        IO.puts("#{member.name} borrowed: #{book.title}")
        :ok
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
  def return_book(member_id, book_id) do
    books = Data.get_books()
    members = Data.get_members()

    with {:ok, book} <- Map.fetch(books, book_id),
         {:ok, member} <- Map.fetch(members, member_id) do
      if not book.available do
        Data.put_book(book_id, %{book | available: true})
        Data.put_member(member_id, %{member | borrowed_books: List.delete(member.borrowed_books, book_id)})
        IO.puts("#{member.name} returned: #{book.title}")
        :ok
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
  def show_available_books do
    IO.puts("Available books:")
    Data.get_books()
    |> Map.values()
    |> Enum.filter(& &1.available)
    |> Enum.each(fn b -> IO.puts("  [#{b.book_id}] #{b.title} by #{b.author}") end)
  end
end

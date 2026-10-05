# In-memory store mirroring Java Data.books/Data.members + LibrarySystem methods.
# Java OOP (mutates objects) -> Elixir (Agent holds state, fns return new state).
defmodule CrudApi.Store do
  use Agent

  def start_link(_),
    do: Agent.start_link(fn -> %{next_id: 1, books: %{}, members: %{}} end, name: __MODULE__)

  # Java: showAvailableBooks / books CRUD
  def list, do: Agent.get(__MODULE__, fn s -> Map.values(s.books) end)

  def list_available,
    do: Agent.get(__MODULE__, fn s -> s.books |> Map.values() |> Enum.filter(&(&1["available"] == true)) end)

  def list_members, do: Agent.get(__MODULE__, fn s -> Map.values(s.members) end)

  def get(id), do: Agent.get(__MODULE__, fn s -> Map.fetch(s.books, id) end)

  # Java: addBook(Book b) { Data.books.put(b.getBookId(), b); }
  def create(%{"title" => title, "author" => author}) do
    Agent.get_and_update(__MODULE__, fn %{next_id: id, books: books} = s ->
      book = %{"id" => id, "title" => title, "author" => author, "available" => true}
      {{:ok, book}, %{s | next_id: id + 1, books: Map.put(books, id, book)}}
    end)
  end

  # Java: registerMember(Member m) { Data.members.put(m.getMemberId(), m); }
  def register_member(%{"name" => name}) do
    Agent.get_and_update(__MODULE__, fn %{next_id: id, members: members} = s ->
      member = %{"id" => id, "name" => name, "borrowed" => []}
      {{:ok, member}, %{s | next_id: id + 1, members: Map.put(members, id, member)}}
    end)
  end

  def update(id, attrs) do
    Agent.get_and_update(__MODULE__, fn %{books: books} = s ->
      case Map.fetch(books, id) do
        :error -> {{:error, :not_found}, s}
        {:ok, book} ->
          nb = Map.merge(book, Map.take(attrs, ["title", "author"]))
          {{:ok, nb}, %{s | books: Map.put(books, id, nb)}}
      end
    end)
  end

  def delete(id) do
    Agent.get_and_update(__MODULE__, fn %{books: books} = s ->
      case Map.pop(books, id) do
        {nil, _} -> {{:error, :not_found}, s}
        {book, rest} -> {{:ok, book}, %{s | books: rest}}
      end
    end)
  end

  # Java: borrowBook(memberId, bookId) { null checks; isAvailable(); setAvailable(false); list.add }
  def borrow_book(member_id, book_id) do
    Agent.get_and_update(__MODULE__, fn %{books: books, members: members} = s ->
      with {:ok, book} <- Map.fetch(books, book_id),
           {:ok, member} <- Map.fetch(members, member_id),
           true <- book["available"] == true do
        nb = Map.put(book, "available", false)
        nm = Map.update!(member, "borrowed", &[book_id | &1])
        ns = %{s | books: Map.put(books, book_id, nb), members: Map.put(members, member_id, nm)}
        {{:ok, %{"book" => nb, "member" => nm}}, ns}
      else
        :error -> {{:error, :not_found}, s}
        false -> {{:error, :unavailable}, s}
      end
    end)
  end

  # Java: returnBook(memberId, bookId) { reverse of borrow }
  def return_book(member_id, book_id) do
    Agent.get_and_update(__MODULE__, fn %{books: books, members: members} = s ->
      with {:ok, book} <- Map.fetch(books, book_id),
           {:ok, member} <- Map.fetch(members, member_id),
           false <- book["available"] == true do
        nb = Map.put(book, "available", true)
        nm = Map.update!(member, "borrowed", &List.delete(&1, book_id))
        ns = %{s | books: Map.put(books, book_id, nb), members: Map.put(members, member_id, nm)}
        {{:ok, %{"book" => nb, "member" => nm}}, ns}
      else
        :error -> {{:error, :not_found}, s}
        true -> {{:error, :not_borrowed}, s}
      end
    end)
  end

  def reset, do: Agent.update(__MODULE__, fn _ -> %{next_id: 1, books: %{}, members: %{}} end)
end

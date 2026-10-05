defmodule LibrarySystem.Data do
  @moduledoc "Mirrors DataSet/Data.java static HashMaps. Agent holds %{books: %{id=>%Book{}}, members: %{id=>%Member{}}}."
  use Agent

  def start_link(_), do: Agent.start_link(fn -> %{books: %{}, members: %{}} end, name: __MODULE__)

  def reset, do: Agent.update(__MODULE__, fn _ -> %{books: %{}, members: %{}} end)
  def get_books, do: Agent.get(__MODULE__, & &1.books)
  def get_members, do: Agent.get(__MODULE__, & &1.members)
  def put_book(id, book), do: Agent.update(__MODULE__, fn s -> %{s | books: Map.put(s.books, id, book)} end)
  def put_member(id, member), do: Agent.update(__MODULE__, fn s -> %{s | members: Map.put(s.members, id, member)} end)
end

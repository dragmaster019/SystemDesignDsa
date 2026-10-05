defmodule LibrarySystem.Member do
  @moduledoc "Mirrors Model/Member.java. borrowedBooks ArrayList -> list of book_ids."
  defstruct [:member_id, :name, borrowed_books: []]

  def new(member_id, name),
    do: %__MODULE__{member_id: member_id, name: name}
end

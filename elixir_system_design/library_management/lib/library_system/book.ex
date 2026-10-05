defmodule LibrarySystem.Book do
  @moduledoc "Mirrors Model/Book.java. Struct instead of class + getters/setters."
  defstruct [:book_id, :title, :author, available: true]

  def new(book_id, title, author),
    do: %__MODULE__{book_id: book_id, title: title, author: author}
end

# HTTP layer only. Plug.Router (no Phoenix). JSON via Jason.
defmodule CrudApi.Router do
  use Plug.Router

  plug Plug.Parsers, parsers: [:json], json_decoder: Jason
  plug :match
  plug :dispatch

  get "/books" do
    books = if conn.query_params["available"] == "true", do: CrudApi.Store.list_available(), else: CrudApi.Store.list()
    send_json(conn, 200, books)
  end

  get "/books/:id" do
    case parse_id(id) do
      {:ok, int} ->
        case CrudApi.Store.get(int) do
          {:ok, book} -> send_json(conn, 200, book)
          :error -> send_json(conn, 404, %{"error" => "not found"})
        end
      :error -> send_json(conn, 400, %{"error" => "bad id"})
    end
  end

  post "/books" do
    case conn.body_params do
      %{"title" => _, "author" => _} = p ->
        {:ok, book} = CrudApi.Store.create(p)
        send_json(conn, 201, book)
      _ -> send_json(conn, 400, %{"error" => "need title + author"})
    end
  end

  put "/books/:id" do
    with {:ok, int} <- parse_id(id),
         {:ok, book} <- CrudApi.Store.update(int, conn.body_params) do
      send_json(conn, 200, book)
    else
      :error -> send_json(conn, 400, %{"error" => "bad id"})
      {:error, :not_found} -> send_json(conn, 404, %{"error" => "not found"})
    end
  end

  delete "/books/:id" do
    with {:ok, int} <- parse_id(id),
         {:ok, book} <- CrudApi.Store.delete(int) do
      send_json(conn, 200, book)
    else
      :error -> send_json(conn, 400, %{"error" => "bad id"})
      {:error, :not_found} -> send_json(conn, 404, %{"error" => "not found"})
    end
  end

  # Java: registerMember -> POST /members {"name": "Sarthak"}
  post "/members" do
    case conn.body_params do
      %{"name" => _} = p ->
        {:ok, member} = CrudApi.Store.register_member(p)
        send_json(conn, 201, member)
      _ -> send_json(conn, 400, %{"error" => "need name"})
    end
  end

  get "/members" do
    send_json(conn, 200, CrudApi.Store.list_members())
  end

  # Java: borrowBook(memberId, bookId) -> POST /borrow {"member_id": 2, "book_id": 1}
  post "/borrow" do
    with %{"member_id" => mid, "book_id" => bid} <- conn.body_params,
         {:ok, result} <- CrudApi.Store.borrow_book(mid, bid) do
      send_json(conn, 200, result)
    else
      {:error, :not_found} -> send_json(conn, 404, %{"error" => "Book or Member not found."})
      {:error, :unavailable} -> send_json(conn, 422, %{"error" => "Book is not available."})
      _ -> send_json(conn, 400, %{"error" => "need member_id + book_id"})
    end
  end

  # Java: returnBook(memberId, bookId) -> POST /return
  post "/return" do
    with %{"member_id" => mid, "book_id" => bid} <- conn.body_params,
         {:ok, result} <- CrudApi.Store.return_book(mid, bid) do
      send_json(conn, 200, result)
    else
      {:error, :not_found} -> send_json(conn, 404, %{"error" => "Book or Member not found."})
      {:error, :not_borrowed} -> send_json(conn, 422, %{"error" => "Book was not borrowed."})
      _ -> send_json(conn, 400, %{"error" => "need member_id + book_id"})
    end
  end

  match _, do: send_json(conn, 404, %{"error" => "route not found"})

  defp send_json(conn, status, data) do
    conn |> put_resp_content_type("application/json") |> send_resp(status, Jason.encode!(data))
  end

  defp parse_id(id) do
    case Integer.parse(id) do
      {int, ""} -> {:ok, int}
      _ -> :error
    end
  end
end

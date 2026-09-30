# Copy to lib/fintech_web/controllers/shape_controller.ex
# Purpose: authenticated shape proxy — device never talks to Electric directly.
defmodule FintechWeb.ShapeController do
  use FintechWeb, :controller

  @electric "http://electric:5133/v1/shape"
  @allowed %{"ledger_entries" => "branch_id", "accounts" => "branch_id"}

  def show(conn, %{"table" => table} = params) do
    branch = conn.assigns[:branch_id] || "blr" # set by auth plug in real app

    case Map.fetch(@allowed, table) do
      :error ->
        conn |> put_status(403) |> json(%{error: "shape not allowed"})

      {:ok, scope_col} ->
        where = "#{scope_col} = '#{branch}'"
        query = URI.encode_query(%{"table" => table, "where" => where, "offset" => params["offset"] || "-1"})
        url = "#{@electric}?#{query}"

        # In prod use Finch/Req streaming; simplified with :httpc for learning:
        {:ok, {{_, status, _}, headers, body}} = :httpc.request(String.to_charlist(url))

        conn
        |> put_resp_header("electric-offset", to_string(header(headers, 'electric-offset')))
        |> put_resp_header("electric-handle", to_string(header(headers, 'electric-handle')))
        |> send_resp(status, body)
    end
  end

  defp header(headers, key) do
    case List.keyfind(headers, key, 0) do
      {_, v} -> v
      nil -> ""
    end
  end
end

# Copy into lib/fintech_web/controllers/transfer_controller.ex
defmodule FintechWeb.TransferController do
  use FintechWeb, :controller
  alias Fintech.Ledger

  # POST /api/transfers
  # Headers: Idempotency-Key: <uuid> (required for real money)
  def create(conn, %{"from" => from, "to" => to, "amount_paise" => amount}) do
    key = get_req_header(conn, "idempotency-key") |> List.first()

    if is_nil(key) do
      conn |> put_status(400) |> json(%{error: "Idempotency-Key header required"})
    else
      case Ledger.transfer(from, to, amount, key) do
        {:ok, %{entry: entry}} ->
          conn |> put_status(201) |> json(%{id: entry.id, status: "posted"})
        {:error, :entry, %{errors: [idempotency_key: _]}, _} ->
          # duplicate key -> fetch original, return 200 (safe retry)
          conn |> put_status(200) |> json(%{status: "duplicate", message: "already processed"})
        {:error, _, reason, _} ->
          conn |> put_status(422) |> json(%{error: to_string(reason)})
      end
    end
  end
end

# --- Ecto schemas (lib/fintech/ledger/*.ex) ---
# defmodule Fintech.Ledger.Entry do
#   use Ecto.Schema
#   import Ecto.Changeset
#   schema "ledger_entries" do
#     field :from_id, :string
#     field :to_id, :string
#     field :amount_paise, :integer
#     field :idempotency_key, :string
#     timestamps()
#   end
#   def changeset(e, attrs) do
#     e
#     |> cast(attrs, [:from_id, :to_id, :amount_paise, :idempotency_key])
#     |> validate_required([:from_id, :to_id, :amount_paise, :idempotency_key])
#     |> validate_number(:amount_paise, greater_than: 0)
#     |> unique_constraint(:idempotency_key)  # <- the money-saver
#   end
# end

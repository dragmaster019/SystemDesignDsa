defmodule CrudApi.Application do
  # See https://hexdocs.pm/elixir/Application.html
  # for more information on OTP Applications
  @moduledoc false

  use Application

  @impl true
  def start(_type, _args) do
    children =
      if Mix.env() == :test do
        [CrudApi.Store] # no HTTP listener in tests (avoids port clash)
      else
        [CrudApi.Store, {Bandit, plug: CrudApi.Router, port: 4000}]
      end

    # See https://hexdocs.pm/elixir/Supervisor.html
    # for other strategies and supported options
    opts = [strategy: :one_for_one, name: CrudApi.Supervisor]
    Supervisor.start_link(children, opts)
  end
end

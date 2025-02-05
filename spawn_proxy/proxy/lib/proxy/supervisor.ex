defmodule Proxy.Supervisor do
  @moduledoc """
  Proxy Application Root Supervisor.
  """
  use Supervisor

  def child_spec(config) do
    %{
      id: __MODULE__,
      start: {__MODULE__, :start_link, [config]},
      shutdown: 120_000
    }
  end

  def start_link(config) do
    Supervisor.start_link(__MODULE__, config, name: __MODULE__)
  end

  @impl true
  def init(config) do
    children = [
      {Sidecar.Supervisor, config},
      {Bandit, get_bandit_options(config)}
    ]

    Supervisor.init(children, strategy: :one_for_one)
  end

  defp get_bandit_options(config) do
    if config.proxy_uds_enable == "true" do
      get_uds_options(config)
    else
      get_tcp_options(config)
    end
    |> Keyword.merge(plug: Proxy.Router, scheme: :http)
  end

  defp get_uds_options(config) do
    [
      port: 0,
      thousand_island_options: [
        transport_options: [ip: {:local, config.proxy_sock_addr}]
      ]
    ]
  end

  defp get_tcp_options(config) do
    [
      port: config.http_port
    ]
  end
end

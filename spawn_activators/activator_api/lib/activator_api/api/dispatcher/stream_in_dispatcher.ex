defmodule ActivatorAPI.Api.Dispatcher.StreamInDispatcher do
  @moduledoc """
  `StreamInDispatcher`
  """
  @behaviour ActivatorAPI.Api.Dispatcher

  require Logger

  @impl true
  def dispatch(_message, _stream, opts \\ []) do
  end
end

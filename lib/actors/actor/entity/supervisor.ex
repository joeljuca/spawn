defmodule Actors.Actor.Entity.Supervisor do
  use DynamicSupervisor
  require Logger

  alias Eigr.Functions.Protocol.Actors.{Actor, ActorSystem}
  alias Actors.Actor.Entity.EntityState

  def child_spec() do
    {
      PartitionSupervisor,
      child_spec: DynamicSupervisor, name: __MODULE__, max_restarts: 100
    }
  end

  def start_link(_) do
    DynamicSupervisor.start_link(
      __MODULE__,
      [
        shutdown: 120_000,
        strategy: :one_for_one
      ],
      name: __MODULE__
    )
  end

  @impl true
  def init(args), do: DynamicSupervisor.init(args)

  @doc """
  Adds a Actor to the dynamic supervisor.
  """
  @spec lookup_or_create_actor(ActorSystem.t(), Actor.t(), any()) :: {:ok, any}
  def lookup_or_create_actor(system, actor, opts \\ [])

  def lookup_or_create_actor(system, %Actor{} = actor, opts) when is_nil(system) do
    entity_state = %EntityState{system: nil, actor: actor, opts: opts}

    child_spec = %{
      id: Actors.Actor.Entity,
      start: {Actors.Actor.Entity, :start_link, [entity_state]},
      restart: :transient
    }

    case DynamicSupervisor.start_child(via(child_spec), child_spec) do
      {:error, {:already_started, pid}} ->
        {:ok, pid}

      {:ok, pid} ->
        {:ok, pid}

      {:error, {:name_conflict, {{Actors.Actor.Entity, name}, _f}, _registry, pid}} ->
        Logger.warning("Name conflict on start Actor #{name} from PID #{inspect(pid)}.")

        :ignore
    end
  end

  def lookup_or_create_actor(
        %ActorSystem{name: actor_system} = _system,
        %Actor{} = actor,
        opts
      ) do
    entity_state = %EntityState{system: actor_system, actor: actor, opts: opts}

    child_spec = %{
      id: Actors.Actor.Entity,
      start: {Actors.Actor.Entity, :start_link, [entity_state]},
      restart: :transient
    }

    case DynamicSupervisor.start_child(via(child_spec), child_spec) do
      {:error, {:already_started, pid}} ->
        {:ok, pid}

      {:ok, pid} ->
        {:ok, pid}

      {:error, {:name_conflict, {{Actors.Actor.Entity, name}, _f}, _registry, pid}} ->
        Logger.warning("Name conflict on start Actor #{name} from PID #{inspect(pid)}.")

        :ignore
    end
  end

  defp get_key(spec), do: :erlang.phash2(Map.drop(spec, [:id]))

  defp via(spec), do: {:via, PartitionSupervisor, {__MODULE__, get_key(spec)}}
end

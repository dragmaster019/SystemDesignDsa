# 11 - The ONE pattern for building ANY feature with N functions. Run: elixir 11_feature_template.exs
# WHY a new map every time: Elixir can't mutate, so `state` (today's mini-database)
# goes IN, a new `state` (tomorrow's) comes OUT. Caller saves it with `state = ...`.

defmodule Shop do
  # 1. STATE = one map, one key per collection (like one table per HashMap in Java)
  def new, do: %{items: %{}, orders: %{}}

  # 2. ADD = pull key from item, Map.put it in, swap back with |
  def add_item(state, %{id: id} = item),
    do: %{state | items: Map.put(state.items, id, item)}

  # 3. GET = Map.fetch (returns {:ok, x} or :error, never null)
  def get_item(state, id), do: Map.fetch(state.items, id)

  # 4. UPDATE = fetch, copy with |, put back. Error if missing.
  def rename_item(state, id, new_name) do
    with {:ok, item} <- Map.fetch(state.items, id) do
      {:ok, %{state | items: Map.put(state.items, id, %{item | name: new_name})}}
    end
  end

  # 5. DELETE = Map.pop
  def remove_item(state, id) do
    {gone, rest} = Map.pop(state.items, id)
    if gone == nil, do: {:error, :not_found}, else: {:ok, %{state | items: rest}}
  end

  # 6. LIST = values + Enum
  def list_items(state), do: Map.values(state.items)

  # 7. NEW FEATURE = copy any block above, change the key. That's it.
  # Example: orders work EXACTLY like items, only the key differs:
  def add_order(state, %{id: id} = order),
    do: %{state | orders: Map.put(state.orders, id, order)}

  def list_orders(state), do: Map.values(state.orders)
end

# --- demo: N functions, same 3 lines each ---
state = Shop.new()
state = Shop.add_item(state, %{id: 1, name: "pen", price: 10})
state = Shop.add_item(state, %{id: 2, name: "book", price: 50})
IO.inspect(Shop.list_items(state), label: "items")
IO.inspect(Shop.get_item(state, 1), label: "get 1")
{:ok, state} = Shop.rename_item(state, 1, "red pen")
{:ok, state} = Shop.remove_item(state, 2)
state = Shop.add_order(state, %{id: 101, item_id: 1, qty: 3})
IO.inspect(Shop.list_items(state), label: "items now")
IO.inspect(Shop.list_orders(state), label: "orders now")
IO.puts("RULE: (state, args) in -> new state out. New feature = copy block, change key.")

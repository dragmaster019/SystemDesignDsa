# 02 - List, Tuple, Map: create, access, update. Run: elixir 02_lists_tuples_maps.exs

IO.puts("--- LIST (like ArrayList, linked) ---")
nums = [10, 20, 30, 40]
IO.inspect(nums, label: "nums")

# access: head | tail, at index
[head | tail] = nums
IO.puts("head=#{head} tail=#{inspect(tail)}")
IO.puts("at index 0=#{Enum.at(nums, 0)} index 2=#{Enum.at(nums, 2)}")
IO.puts("first=#{List.first(nums)} last=#{List.last(nums)} length=#{length(nums)}")

# add / remove (returns NEW list, old unchanged - immutable!)
nums2 = [5 | nums]                    # prepend (fast)
IO.inspect(nums2, label: "prepend 5")
IO.inspect(nums ++ [50], label: "append 50")
IO.inspect(List.delete(nums, 20), label: "delete 20")
IO.inspect(List.replace_at(nums, 1, 99), label: "set idx1=99")

IO.puts("\n--- TUPLE (fixed size, like pair/record) ---")
t = {:ok, 5000}
IO.inspect(t, label: "t")
IO.puts("elem 0=#{elem(t, 0)} elem 1=#{elem(t, 1)}")  # access by index
t2 = put_elem(t, 1, 9999)  # returns new tuple
IO.inspect(t2, label: "updated tuple")
# tuples used for status: {:ok, data} / {:error, reason}

IO.puts("\n--- MAP (like HashMap) ---")
user = %{id: "alice", balance: 5000, branch: "blr"}
IO.inspect(user, label: "user")

# access 3 ways
IO.puts("dot: #{user.balance}")
IO.puts("bracket: #{user[:balance]}")
IO.puts("get: #{Map.get(user, :balance)}")
IO.puts("missing with default: #{Map.get(user, :phone, "N/A")}")

# update (returns NEW map)
user2 = %{user | balance: 4500}          # update existing key
IO.inspect(user2, label: "balance updated")
user3 = Map.put(user2, :phone, "999")    # add new key
IO.inspect(user3, label: "phone added")

# nested access
acc = %{owner: %{name: "bob", kyc: %{status: "ok"}}}
IO.puts("nested: #{acc.owner.kyc.status}")
IO.puts("get_in: #{get_in(acc, [:owner, :kyc, :status])}")

IO.puts("\n--- KEYWORD LIST (options, like kwargs) ---")
opts = [from: "a", to: "b", amount: 100]
IO.inspect(opts, label: "opts")
IO.puts("amount=#{opts[:amount]}")

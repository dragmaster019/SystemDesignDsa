#1.var

var1= "sarthak"
var2 = "has eaten"

IO.puts(var1 <> " " <> var2)

name = "sarkman"

IO.puts(" the name is #{name}")

#input = IO.gets("choose the name ")

#IO.puts("hi #{input}")

#2.List

nums = [1,2,3,4,5]

IO.inspect(nums, label: "list are")

IO.puts("third index is #{Enum.at(nums,2)}")
IO.puts("length is #{length(nums)}")

IO.inspect(List.delete_at(nums,3))

#3.tuple

t = {:ok, 5000}

IO.inspect("elem 1 is #{elem(t,0)} elem 2 is #{elem(t,1)}")

IO.inspect(put_elem(t,1,500))
IO.inspect(Tuple.append(t,3000))
IO.inspect(Tuple.delete_at(t,1))


#4.maps

map = %{name: "sarthak", age: 24 }

IO.inspect(map.name)
IO.inspect(Map.put(map,:hobby,"game"))

#5.set

s = MapSet.new([1,2,2,3,4])

IO.inspect(s, label: "set members are")

s2 = MapSet.put(s, 5)

IO.inspect(s2, label: "new elem added")

Enum.each(s2, fn x -> IO.puts("each element are #{x}") end)

evens = Enum.filter(s2, fn x -> rem(x, 2) == 0 end)
Enum.each(evens, fn x -> IO.puts("even numbers are #{x}") end)

Enum.each(s2, fn x ->
  if rem(x, 2) == 0, do: IO.puts("even numbers are #{x}")
end)


#6.stack

stack = []
stack = [10 | stack]
stack = [20 | stack]

[top | rest] = stack

IO.puts("the top is #{top}")

#7.queue
 
q = :queue.new()

#8.class

defmodule User do

 defstruct [:id, balance: 0]
 def new(id,bal), do: %User{id: id, balance: bal}
 def debit(%User{balance: b} = u,amt) when b>=amt, do: {:ok, %{u | balance: b - amt}}
 def debit(_,_) ,do: {:error,:insuficiant_fund}

end

u1 = User.new("sarthak", 5000)
u2 = User.new("sayak", 10000)

IO.inspect([u1,u2], label: "users are")

IO.inspect(User.debit(u1,3000) , label: "rest balance is")
IO.inspect(User.debit(u2,3000) , label: "rest balance is")
IO.inspect(User.debit(u1,7000) , label: "rest balance is")


defmodule CrudApiTest do
  use ExUnit.Case
  doctest CrudApi

  test "greets the world" do
    assert CrudApi.hello() == :world
  end
end

defmodule LibrarySystemTest do
  use ExUnit.Case
  import ExUnit.CaptureIO

  alias LibrarySystem.{Book, Member, Data}

  setup do
    # Data agent is started by the Application; reset it per test
    Data.reset()
    :ok
  end

  test "full Main.java flow: add, register, borrow, return" do
    LibrarySystem.add_book(Book.new(1, "Clean Code", "Robert Martin"))
    LibrarySystem.add_book(Book.new(2, "System Design", "Alex Xu"))
    LibrarySystem.register_member(Member.new(101, "Sarthak"))
    LibrarySystem.register_member(Member.new(102, "Rahul"))

    assert :ok = LibrarySystem.borrow_book(101, 1)
    assert {:error, :unavailable} = LibrarySystem.borrow_book(102, 1)

    out = capture_io(fn -> LibrarySystem.show_available_books() end)
    refute out =~ "Clean Code"
    assert out =~ "System Design"

    assert :ok = LibrarySystem.return_book(101, 1)

    out2 = capture_io(fn -> LibrarySystem.show_available_books() end)
    assert out2 =~ "Clean Code"
  end

  test "borrow missing book or member" do
    assert {:error, :not_found} = LibrarySystem.borrow_book(999, 999)
  end
end

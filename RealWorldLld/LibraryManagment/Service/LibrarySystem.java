import java.util.*;

public class LibrarySystem {

    // addBook — store the book in the map using its id as the key
    public void addBook(Book b) {
        Data.books.put(b.getBookId(), b);
    }

    // registerMember — same pattern, store member using memberId as key
    public void registerMember(Member m) {
        Data.members.put(m.getMemberId(), m);
    }

    // borrowBook — fetch both objects, check availability, update state
    public void borrowBook(int memberId, int bookId) {
        Book book = Data.books.get(bookId);         // fetch Book object from map
        Member member = Data.members.get(memberId); // fetch Member object from map

        if (book == null || member == null) {
            System.out.println("Book or Member not found.");
            return;
        }

        if (book.isAvailable()) {                       // getter — read the field
            book.setAvailable(false);                   // setter — change the field
            member.getBorrowedBooks().add(book);        // getter returns the list, then add to it
            System.out.println(member.getName() + " borrowed: " + book.getTitle());
        } else {
            System.out.println("Book is not available.");
        }
    }

    // returnBook — reverse of borrowBook
    public void returnBook(int memberId, int bookId) {
        Book book = Data.books.get(bookId);
        Member member = Data.members.get(memberId);

        if (book == null || member == null) {
            System.out.println("Book or Member not found.");
            return;
        }

        if (!book.isAvailable()) {                      // ! means "if NOT available"
            book.setAvailable(true);                    // mark free again
            member.getBorrowedBooks().remove(book);     // remove from member's list
            System.out.println(member.getName() + " returned: " + book.getTitle());
        } else {
            System.out.println("Book was not borrowed.");
        }
    }

    // showAvailableBooks — loop all books, print only available ones
    public void showAvailableBooks() {
        System.out.println("Available books:");
        for (Book book : Data.books.values()) {         // .values() gives all Book objects
            if (book.isAvailable()) {
                System.out.println("  [" + book.getBookId() + "] "
                        + book.getTitle() + " by " + book.getAuthor());
            }
        }
    }
}

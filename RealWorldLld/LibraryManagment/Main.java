public class Main {

    public static void main(String[] args) {

        LibrarySystem library = new LibrarySystem();

        // Step 1: add books
        library.addBook(new Book(1, "Clean Code", "Robert Martin"));
        library.addBook(new Book(2, "System Design", "Alex Xu"));
        library.addBook(new Book(3, "Effective Java", "Joshua Bloch"));

        // Step 2: register members
        library.registerMember(new Member(101, "Sarthak"));
        library.registerMember(new Member(102, "Rahul"));

        // Step 3: show all available books
        library.showAvailableBooks();

        // Step 4: borrow
        System.out.println();
        library.borrowBook(101, 1);   // Sarthak borrows Clean Code
        library.borrowBook(102, 1);   // Rahul tries same book — not available

        // Step 5: show available again (Clean Code should be gone)
        System.out.println();
        library.showAvailableBooks();

        // Step 6: return
        System.out.println();
        library.returnBook(101, 1);   // Sarthak returns Clean Code

        // Step 7: show available again (Clean Code back)
        System.out.println();
        library.showAvailableBooks();
    }
}

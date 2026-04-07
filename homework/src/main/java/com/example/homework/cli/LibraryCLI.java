package com.example.homework.cli;

import com.example.homework.model.Author;
import com.example.homework.model.Book;
import com.example.homework.model.Issue;
import com.example.homework.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class LibraryCLI implements CommandLineRunner {

    private final LibraryService libraryService;

    private final Scanner scanner = new Scanner(System.in);

    public LibraryCLI(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @Override
    public void run(String... args) {
        while (true) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            System.out.println();
            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> searchByTitle();
                    case 3 -> searchByCategory();
                    case 4 -> searchByAuthor();
                    case 5 -> listAllBooks();
                    case 6 -> issueBook();
                    case 7 -> listByUsn();
                    case 8 -> listDueToday();
                    case 9 -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid option. Please choose 1-9.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    // ── Menu ──────────────────────────────────────────────────

    private void printMenu() {
        System.out.println("========== Library Management System ==========");
        System.out.println("1. Add a book");
        System.out.println("2. Search book by title");
        System.out.println("3. Search book by category");
        System.out.println("4. Search book by Author");
        System.out.println("5. List all books along with author");
        System.out.println("6. Issue book to student");
        System.out.println("7. List books by USN");
        System.out.println("8. List books to be returned today (Bonus)");
        System.out.println("9. Exit");
        System.out.println("===============================================");
    }

    // ── Actions ───────────────────────────────────────────────

    private void addBook() {
        String isbn    = readString("Enter isbn: ");
        String title   = readString("Enter title: ");
        String cat     = readString("Enter category: ");
        String aName   = readString("Enter Author name: ");
        String aEmail  = readString("Enter Author mail: ");
        int qty        = readInt("Enter number of books: ");

        libraryService.addBook(isbn, title, cat, qty, aName, aEmail);
        System.out.println("Book added successfully!");
    }

    private void searchByTitle() {
        String title = readString("Enter title: ");
        List<Book> books = libraryService.searchByTitle(title);
        printBookHeader();
        books.forEach(this::printBook);
        if (books.isEmpty()) System.out.println("No books found.");
    }

    private void searchByCategory() {
        String cat = readString("Enter category: ");
        List<Book> books = libraryService.searchByCategory(cat);
        printBookHeader();
        books.forEach(this::printBook);
        if (books.isEmpty()) System.out.println("No books found.");
    }

    private void searchByAuthor() {
        String name = readString("Enter name: ");
        List<Author> authors = libraryService.searchByAuthor(name);
        printBookHeader();
        authors.stream().map(Author::getAuthorBook).forEach(this::printBook);
        if (authors.isEmpty()) System.out.println("No books found.");
    }

    private void listAllBooks() {
        List<Author> authors = libraryService.listAllBooksWithAuthors();
        System.out.printf("%-25s %-25s %-15s %-12s %-20s %-30s%n",
                "Book ISBN", "Book Title", "Category", "No of Books", "Author name", "Author mail");
        System.out.println("-".repeat(127));
        for (Author a : authors) {
            Book b = a.getAuthorBook();
            System.out.printf("%-25s %-25s %-15s %-12d %-20s %-30s%n",
                    b.getIsbn(), b.getTitle(), b.getCategory(),
                    b.getQuantity(), a.getName(), a.getEmail());
        }
        if (authors.isEmpty()) System.out.println("No books in library.");
    }

    private void issueBook() {
        String usn   = readString("Enter usn: ");
        String name  = readString("Enter name: ");
        String isbn  = readString("Enter book ISBN: ");
        Issue issue  = libraryService.issueBook(usn, name, isbn);
        System.out.println("Book issued. Return date : " + issue.getReturnDate());
    }

    private void listByUsn() {
        String usn = readString("Enter usn: ");
        List<Issue> issues = libraryService.listBooksByUsn(usn);
        System.out.printf("%-25s %-20s %-25s%n", "Book Title", "Student Name", "Return date");
        System.out.println("-".repeat(70));
        issues.forEach(i -> System.out.printf("%-25s %-20s %-25s%n",
                i.getIssueBook().getTitle(),
                i.getIssueStudent().getName(),
                i.getReturnDate()));
        if (issues.isEmpty()) System.out.println("No issued books found for this USN.");
    }

    private void listDueToday() {
        List<Issue> issues = libraryService.listBooksReturnToday();
        System.out.printf("%-25s %-20s %-25s%n", "Book Title", "Student Name", "Return date");
        System.out.println("-".repeat(70));
        issues.forEach(i -> System.out.printf("%-25s %-20s %-25s%n",
                i.getIssueBook().getTitle(),
                i.getIssueStudent().getName(),
                i.getReturnDate()));
        if (issues.isEmpty()) System.out.println("No books due today.");
    }

    // ── Helpers ───────────────────────────────────────────────

    private void printBookHeader() {
        System.out.printf("%-25s %-25s %-15s %-12s%n",
                "Book ISBN", "Book Title", "Category", "No of Books");
        System.out.println("-".repeat(77));
    }

    private void printBook(Book b) {
        System.out.printf("%-25s %-25s %-15s %-12d%n",
                b.getIsbn(), b.getTitle(), b.getCategory(), b.getQuantity());
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}

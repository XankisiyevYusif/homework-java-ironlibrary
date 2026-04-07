package com.example.homework;

import com.example.homework.model.Author;
import com.example.homework.model.Book;
import com.example.homework.model.Issue;
import com.example.homework.repository.BookRepository;
import com.example.homework.repository.IssueRepository;
import com.example.homework.service.LibraryService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LibraryServiceTest {

    @Autowired private LibraryService libraryService;
    @Autowired private BookRepository bookRepo;
    @Autowired private IssueRepository issueRepo;

    private static final String ISBN   = "TEST-001";
    private static final String ISBN2  = "TEST-002";

    @Test @Order(1)
    void testAddBook() {
        Book book = libraryService.addBook(ISBN, "Test Book", "Fiction", 5,
                "Test Author", "test@test.com");
        assertNotNull(book);
        assertEquals("Test Book", book.getTitle());
        assertEquals(5, book.getQuantity());
    }

    @Test @Order(2)
    void testAddDuplicateBookThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                libraryService.addBook(ISBN, "Duplicate", "Fiction", 3,
                        "Author", "a@a.com"));
    }

    @Test @Order(3)
    void testSearchByTitle() {
        List<Book> results = libraryService.searchByTitle("Test");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(b -> b.getIsbn().equals(ISBN)));
    }

    @Test @Order(4)
    void testSearchByCategory() {
        List<Book> results = libraryService.searchByCategory("Fiction");
        assertFalse(results.isEmpty());
    }

    @Test @Order(5)
    void testSearchByAuthor() {
        List<Author> authors = libraryService.searchByAuthor("Test Author");
        assertFalse(authors.isEmpty());
        assertEquals(ISBN, authors.get(0).getAuthorBook().getIsbn());
    }

    @Test @Order(6)
    void testIssueBook() {
        Issue issue = libraryService.issueBook("USN001", "Jane Doe", ISBN);
        assertNotNull(issue);
        assertNotNull(issue.getReturnDate());

        // Quantity should decrease
        Book book = bookRepo.findById(ISBN).orElseThrow();
        assertEquals(4, book.getQuantity());
    }

    @Test @Order(7)
    void testIssueBookNotFound() {
        assertThrows(IllegalArgumentException.class, () ->
                libraryService.issueBook("USN002", "Bob", "NONEXISTENT-ISBN"));
    }

    @Test @Order(8)
    void testIssueBookNoStock() {
        libraryService.addBook(ISBN2, "Empty Book", "Drama", 0,
                "No Stock Author", "ns@test.com");
        assertThrows(IllegalStateException.class, () ->
                libraryService.issueBook("USN003", "Alice", ISBN2));
    }

    @Test @Order(9)
    void testListBooksByUsn() {
        List<Issue> issues = libraryService.listBooksByUsn("USN001");
        assertFalse(issues.isEmpty());
        assertEquals("Jane Doe", issues.get(0).getIssueStudent().getName());
    }

    @Test @Order(10)
    void testBookQuantityCannotBeNegative() {
        Book b = new Book("X", "Y", "Z", 1);
        assertThrows(IllegalArgumentException.class, () -> b.setQuantity(-1));
    }

    @Test @Order(11)
    void testListAllBooksWithAuthors() {
        List<Author> authors = libraryService.listAllBooksWithAuthors();
        assertFalse(authors.isEmpty());
    }
}

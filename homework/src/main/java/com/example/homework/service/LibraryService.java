package com.example.homework.service;

import com.example.homework.model.Author;
import com.example.homework.model.Book;
import com.example.homework.model.Issue;
import com.example.homework.model.Student;
import com.example.homework.repository.AuthorRepository;
import com.example.homework.repository.BookRepository;
import com.example.homework.repository.IssueRepository;
import com.example.homework.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class LibraryService {

    private final BookRepository bookRepo;
    private final AuthorRepository authorRepo;
    private final StudentRepository studentRepo;
    private final IssueRepository issueRepo;

    public LibraryService(BookRepository bookRepo, AuthorRepository authorRepo, StudentRepository studentRepo, IssueRepository issueRepo) {
        this.bookRepo = bookRepo;
        this.authorRepo = authorRepo;
        this.studentRepo = studentRepo;
        this.issueRepo = issueRepo;
    }

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    public Book addBook(String isbn, String title, String category,
                        int quantity, String authorName, String authorEmail) {
        if (bookRepo.existsById(isbn)) {
            throw new IllegalArgumentException("A book with ISBN " + isbn + " already exists.");
        }
        Book book = new Book(isbn, title, category, quantity);
        bookRepo.save(book);
        Author author = new Author(authorName, authorEmail, book);
        authorRepo.save(author);
        return book;
    }

    public List<Book> searchByTitle(String title) {
        return bookRepo.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchByCategory(String category) {
        return bookRepo.findByCategoryIgnoreCase(category);
    }

    public List<Author> searchByAuthor(String name) {
        return authorRepo.findByNameContainingIgnoreCase(name);
    }

    public List<Author> listAllBooksWithAuthors() {
        return authorRepo.findAll();
    }

    public Issue issueBook(String usn, String studentName, String isbn) {
        Optional<Book> bookOpt = bookRepo.findById(isbn);
        if (bookOpt.isEmpty()) {
            throw new IllegalArgumentException("No book found with ISBN: " + isbn);
        }
        Book book = bookOpt.get();
        if (book.getQuantity() <= 0) {
            throw new IllegalStateException("No copies available for: " + book.getTitle());
        }

        Student student = studentRepo.findById(usn)
                .orElse(new Student(usn, studentName));
        studentRepo.save(student);

        String today = LocalDate.now().format(FMT);
        String returnDate = LocalDate.now().plusDays(7).format(FMT);

        book.setQuantity(book.getQuantity() - 1);
        bookRepo.save(book);

        Issue issue = new Issue(today, returnDate, student, book);
        return issueRepo.save(issue);
    }

    public List<Issue> listBooksByUsn(String usn) {
        return issueRepo.findByIssueStudent_Usn(usn);
    }

    public List<Issue> listBooksReturnToday() {
        String today = LocalDate.now().format(FMT);
        return issueRepo.findByReturnDate(today);
    }
}
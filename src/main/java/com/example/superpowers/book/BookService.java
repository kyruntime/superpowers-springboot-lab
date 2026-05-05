package com.example.superpowers.book;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request) {
        Book book = new Book(request.title(), request.author(), false, Instant.now());
        return BookResponse.from(bookRepository.save(book));
    }
}

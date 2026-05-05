package com.example.superpowers.book;

import java.time.Instant;

public record BookResponse(
        Long id,
        String title,
        String author,
        boolean read,
        Instant createdAt
) {

    static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.isRead(),
                book.getCreatedAt()
        );
    }
}

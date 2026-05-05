package com.example.superpowers.book;

import jakarta.validation.constraints.NotBlank;

public record CreateBookRequest(
        @NotBlank(message = "Title must not be blank")
        String title,
        String author
) {
}

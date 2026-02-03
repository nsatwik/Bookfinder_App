package com.bookfinder.controller;

import com.bookfinder.model.Book;
import com.bookfinder.repository.BookRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/books")
public class BookController {
  private final BookRepository bookRepository;

  public BookController(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @GetMapping
  public List<Book> listBooks() {
    return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
  }

  @GetMapping("/{id}")
  public Book getBook(@PathVariable Long id) {
    return bookRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
  }
}

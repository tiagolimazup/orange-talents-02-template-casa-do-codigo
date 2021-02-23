package br.com.zup.bootcamp.casadocodigo.book;

import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/book")
class BookResource {

    final Books books;

    BookResource(Books books) {
        this.books = books;
    }

    @GetMapping
    ResponseEntity<Collection<BookResponse>> listAll() {
        return ResponseEntity.ok(books.findAll().stream()
                .map(BookResponse::new)
                .collect(toList()));
    }

    @GetMapping("/{id}")
    ResponseEntity<BookDetailsResponse> findById(@PathVariable Long id) {
        return books.findById(id)
                .map(BookDetailsResponse::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

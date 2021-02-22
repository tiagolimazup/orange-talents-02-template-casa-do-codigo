package br.com.zup.bootcamp.casadocodigo.book;

import br.com.zup.bootcamp.casadocodigo.author.Authors;
import br.com.zup.bootcamp.casadocodigo.category.Categories;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
class BookResource {

    final Books books;
    final Authors authors;
    final Categories categories;

    BookResource(Books books, Authors authors, Categories categories) {
        this.books = books;
        this.authors = authors;
        this.categories = categories;
    }

    @PostMapping
    ResponseEntity<?> create(@Valid @RequestBody CreateNewBookRequest request) {
        return request.newBook(authors, categories)
                .map(books::save)
                .map(b -> ResponseEntity.ok().build())
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}

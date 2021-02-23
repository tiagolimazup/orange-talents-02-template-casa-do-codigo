package br.com.zup.bootcamp.casadocodigo.book;

import br.com.zup.bootcamp.casadocodigo.author.Author;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

class BookDetailsResponse {

    @JsonProperty
    final Long id;

    @JsonProperty
    final String title;

    @JsonProperty
    final String about;

    @JsonProperty
    final String summary;

    @JsonProperty
    final BigDecimal price;

    @JsonProperty
    final String isbn;

    @JsonProperty
    final int pages;

    @JsonProperty
    final AuthorDetailsResponse author;

    BookDetailsResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.about = book.getAbout();
        this.summary = book.getSummary();
        this.price = book.getPrice();
        this.isbn = book.getIsbn();
        this.pages = book.getPages();
        this.author = new AuthorDetailsResponse(book.getAuthor());
    }

    class AuthorDetailsResponse {

        @JsonProperty
        final String name;

        @JsonProperty
        final String description;

        private AuthorDetailsResponse(Author author) {
            this.name = author.getName();
            this.description = author.getDescription();
        }
    }
}

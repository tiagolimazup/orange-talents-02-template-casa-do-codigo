package br.com.zup.bootcamp.casadocodigo.book;

import com.fasterxml.jackson.annotation.JsonProperty;

class BookResponse {

    @JsonProperty
    final Long id;

    @JsonProperty
    final String title;

    BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
    }
}

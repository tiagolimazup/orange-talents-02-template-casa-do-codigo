package br.com.zup.bootcamp.casadocodigo.book;

import br.com.zup.bootcamp.casadocodigo.author.Author;
import br.com.zup.bootcamp.casadocodigo.author.Authors;
import br.com.zup.bootcamp.casadocodigo.book.Book.BookBuilder;
import br.com.zup.bootcamp.casadocodigo.category.Categories;
import br.com.zup.bootcamp.casadocodigo.category.Category;
import br.com.zup.bootcamp.casadocodigo.validation.Exists;
import br.com.zup.bootcamp.casadocodigo.validation.UniqueValue;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class CreateNewBookRequest {

    @JsonProperty
    @NotBlank
    @UniqueValue(entity = Book.class, field = "title")
    final String title;

    @JsonProperty
    @NotBlank
    @Size(max = 500)
    final String about;

    @JsonProperty
    final String summary;

    @JsonProperty
    @NotNull
    @Min(20)
    final BigDecimal price;

    @JsonProperty
    @NotNull
    @Min(100)
    final Integer pages;

    @JsonProperty
    @NotBlank
    @UniqueValue(entity = Book.class, field = "isbn")
    final String isbn;

    @JsonProperty
    @NotNull
    @Future
    final LocalDate publishedAt;

    @JsonProperty
    @NotNull
    @Exists(entity = Category.class, field = "id")
    final Long categoryId;

    @JsonProperty
    @NotNull
    @Exists(entity = Author.class, field = "id")
    final Long authorId;

    @JsonCreator
    CreateNewBookRequest(String title, String about, String summary, BigDecimal price, Integer pages, String isbn,
                         LocalDate publishedAt, Long categoryId, Long authorId) {
        this.title = title;
        this.about = about;
        this.summary = summary;
        this.price = price;
        this.pages = pages;
        this.isbn = isbn;
        this.publishedAt = publishedAt;
        this.categoryId = categoryId;
        this.authorId = authorId;
    }

    Optional<Book> newBook(Authors authors, Categories categories) {
        return authors.findById(authorId)
                .flatMap(author -> categories.findById(categoryId)
                        .map(category -> new BookBuilder()
                                .withTitle(title)
                                .withAbout(about)
                                .withSummary(summary)
                                .withPrice(price)
                                .withPages(pages)
                                .withIsbn(isbn)
                                .withPublishedAt(publishedAt)
                                .withAuthor(author)
                                .withCategory(category)
                                .build()));
    }
}

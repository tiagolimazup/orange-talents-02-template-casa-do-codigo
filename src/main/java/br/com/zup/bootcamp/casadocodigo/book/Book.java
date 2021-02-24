package br.com.zup.bootcamp.casadocodigo.book;

import br.com.zup.bootcamp.casadocodigo.author.Author;
import br.com.zup.bootcamp.casadocodigo.category.Category;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String title;

    @NotBlank
    @Column(nullable = false, unique = true, length = 500)
    private String about;

    @Lob
    private String summary;

    @NotNull
    @Column(nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(nullable = false)
    private Integer pages;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String isbn;

    @NotNull
    @Column(nullable = false)
    private LocalDate publishedAt;

    @NotNull
    @ManyToOne
    private Category category;

    @NotNull
    @ManyToOne
    private Author author;

    @Deprecated
    Book() { }

    private Book(String title, String about, String summary, BigDecimal price, int pages, String isbn, LocalDate publishedAt, Category category, Author author) {
        this.title = title;
        this.about = about;
        this.summary = summary;
        this.price = price;
        this.pages = pages;
        this.isbn = isbn;
        this.publishedAt = publishedAt;
        this.category = category;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAbout() {
        return about;
    }

    public String getSummary() {
        return summary;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getPages() {
        return pages;
    }

    public String getIsbn() {
        return isbn;
    }

    public LocalDate getPublishedAt() {
        return publishedAt;
    }

    public Category getCategory() {
        return category;
    }

    public Author getAuthor() {
        return author;
    }

    static class BookBuilder {

        String title;

        String about;

        String summary;

        BigDecimal price;

        int pages;

        String isbn;

        LocalDate publishedAt;

        @ManyToOne
        Category category;

        @ManyToOne
        Author author;

        BookBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        BookBuilder withAbout(String about) {
            this.about = about;
            return this;
        }

        BookBuilder withSummary(String summary) {
            this.summary = summary;
            return this;
        }

        BookBuilder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        BookBuilder withPages(int pages) {
            this.pages = pages;
            return this;
        }

        BookBuilder withIsbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        BookBuilder withPublishedAt(LocalDate publishedAt) {
            this.publishedAt = publishedAt;
            return this;
        }

        BookBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        BookBuilder withAuthor(Author author) {
            this.author = author;
            return this;
        }

        Book build() {
            return new Book(title, about, summary, price, pages, isbn, publishedAt, category, author);
        }
    }
}

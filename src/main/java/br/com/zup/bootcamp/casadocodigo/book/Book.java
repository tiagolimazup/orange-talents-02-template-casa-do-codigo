package br.com.zup.bootcamp.casadocodigo.book;

import br.com.zup.bootcamp.casadocodigo.author.Author;
import br.com.zup.bootcamp.casadocodigo.category.Category;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String about;

    @Lob
    private String summary;

    private BigDecimal price;

    private int pages;

    private String isbn;

    private LocalDate publishedAt;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Author author;

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

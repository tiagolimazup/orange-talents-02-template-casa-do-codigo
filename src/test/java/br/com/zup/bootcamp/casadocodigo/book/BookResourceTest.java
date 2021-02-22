package br.com.zup.bootcamp.casadocodigo.book;

import br.com.zup.bootcamp.casadocodigo.author.Author;
import br.com.zup.bootcamp.casadocodigo.author.Authors;
import br.com.zup.bootcamp.casadocodigo.book.Book.BookBuilder;
import br.com.zup.bootcamp.casadocodigo.category.Categories;
import br.com.zup.bootcamp.casadocodigo.category.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.generate;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
public class BookResourceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Books books;

    @Autowired
    Authors authors;

    @Autowired
    Categories categories;

    @Autowired
    ObjectMapper jsonMapper;

    Author author;

    Category category;

    @BeforeEach
    void setup() {
        author = authors.save(new Author("Tiago de Freitas Lima", "tiago.lima@zup.com.br", "I'm an author"));
        category = categories.save(new Category("Software Engineering"));
    }

    @Test
    void createNewBook() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "A book about Software Engineering.",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isOk());

        assertTrue(books.existsByTitle(createNewBookRequest.title));
    }

    @Test
    void rejectNewBookWhenTitleIsEmpty() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                null,
                "A book about Software Engineering.",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'title')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenTitleAlreadyExists() throws Exception {
        Book book = new BookBuilder()
                .withTitle("Code Complete")
                .withAbout("This is a book about about Software Engineering...Etc etc etc...")
                .withSummary("This is a book about about Software Engineering...Etc etc etc...")
                .withPrice(BigDecimal.valueOf(99.99))
                .withPages(567)
                .withIsbn("123-4-56-78910-0")
                .withPublishedAt(LocalDate.now().plusWeeks(2))
                .withCategory(category)
                .withAuthor(author)
                .build();

        books.save(book);

        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                book.getTitle(),
                book.getAbout(),
                book.getSummary(),
                book.getPrice(),
                book.getPages(),
                book.getIsbn(),
                book.getPublishedAt(),
                book.getCategory().getId(),
                book.getAuthor().getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'title')].message").value("already exists"));

        assertTrue(books.count() == 1);
    }

    @Test
    void rejectNewBookWhenAboutIsEmpty() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                null,
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'about')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenAboutIsGreatherThan500Characters() throws Exception {
        String about = generate(() -> "a").limit(600).collect(joining());

        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                about,
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'about')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenPriceIsEmpty() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                null,
                null,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'price')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenPriceIsSmallerThan20() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(19.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'price')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenPagesIsEmpty() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                null,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'pages')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenPagesIsSmallerThan100() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                99,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'pages')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenIsbnIsEmpty() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                null,
                LocalDate.now().plusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'isbn')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenIsbnAlreadyExists() throws Exception {
        Book book = new BookBuilder()
                .withTitle("Code Complete")
                .withAbout("This is a book about about Software Engineering...Etc etc etc...")
                .withSummary("This is a book about about Software Engineering...Etc etc etc...")
                .withPrice(BigDecimal.valueOf(99.99))
                .withPages(567)
                .withIsbn("123-4-56-78910-0")
                .withPublishedAt(LocalDate.now().plusWeeks(2))
                .withCategory(category)
                .withAuthor(author)
                .build();

        books.save(book);

        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                book.getTitle(),
                book.getAbout(),
                book.getSummary(),
                book.getPrice(),
                book.getPages(),
                book.getIsbn(),
                book.getPublishedAt(),
                book.getCategory().getId(),
                book.getAuthor().getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'isbn')].message").value("already exists"));

        assertTrue(books.count() == 1);
    }

    @Test
    void rejectNewBookWhenPublishedIsEmpty() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                null,
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'publishedAt')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenPublishedAtIsInThePast() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().minusWeeks(2),
                category.getId(),
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'publishedAt')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenCategoryIsEmpty() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().minusWeeks(2),
                null,
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'categoryId')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenCategoryNotExists() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().minusWeeks(2),
                1234l,
                author.getId());

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'categoryId')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenAuthorIsEmpty() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                null);

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'authorId')]").exists());

        assertTrue(books.count() == 0);
    }

    @Test
    void rejectNewBookWhenAuthorNotExists() throws Exception {
        CreateNewBookRequest createNewBookRequest = new CreateNewBookRequest(
                "Code Complete",
                "This is a book about about Software Engineering...Etc etc etc...",
                "This is a book about about Software Engineering...Etc etc etc...",
                BigDecimal.valueOf(99.99),
                567,
                "123-4-56-78910-0",
                LocalDate.now().plusWeeks(2),
                category.getId(),
                1234l);

        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(createNewBookRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'authorId')]").exists());

        assertTrue(books.count() == 0);
    }

    private String asJson(CreateNewBookRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}

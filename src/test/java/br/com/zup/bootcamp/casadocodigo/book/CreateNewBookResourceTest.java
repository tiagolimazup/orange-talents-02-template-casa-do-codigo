package br.com.zup.bootcamp.casadocodigo.book;

import br.com.zup.bootcamp.casadocodigo.author.Author;
import br.com.zup.bootcamp.casadocodigo.author.Authors;
import br.com.zup.bootcamp.casadocodigo.book.Book.BookBuilder;
import br.com.zup.bootcamp.casadocodigo.category.Categories;
import br.com.zup.bootcamp.casadocodigo.category.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
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
public class CreateNewBookResourceTest {

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
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        assertTrue(books.existsByTitle(createNewBookRequest.getTitle()));
    }

    @Nested
    class Restrictions {

        @ParameterizedTest(name = "[{index}] {0}")
        @ArgumentsSource(CreateNewBookRestrictionsArguments.class)
        @DisplayName("reject a new book when:")
        void rejectNewBookWhen(String title, CreateNewBookRequest request, String jsonPath, String expectedMessage) throws Exception {
            mockMvc.perform(post("/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(jsonPath).value(expectedMessage));

            assertTrue(books.count() == 0);
        }

        @Nested
        class WhenBookAlreadyExists {

            @ParameterizedTest(name = "[{index}] {0}")
            @ArgumentsSource(WhenBookAlreadyExistsArguments.class)
            @DisplayName("when book already exists")
            void rejectNewBookWhen(String title, Book book, String jsonPath, String expectedMessage) throws Exception {
                authors.saveAndFlush(book.getAuthor());
                categories.saveAndFlush(book.getCategory());
                books.saveAndFlush(book);

                CreateNewBookRequest request = new CreateNewBookRequest(book);

                mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath(jsonPath).value(expectedMessage));

                assertTrue(books.count() == 1);
            }
        }

        @Nested
        class Dependencies {

            @ParameterizedTest(name = "[{index}] {0}")
            @ArgumentsSource(BookDependenciesArguments.class)
            @DisplayName("when book depends of that, but:")
            void rejectNewBookWhen(String title, CreateNewBookRequest request, String jsonPath, String expectedMessage) throws Exception {
                mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath(jsonPath).value(expectedMessage));

                assertTrue(books.count() == 0);
            }
        }
    }

    static class CreateNewBookRestrictionsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            CreateNewBookRequest request = new CreateNewBookRequest(null, null, null,
                    null, null, null, null, null, null);

            return Stream.of(Arguments.of("title is empty", request, "$.errors[?(@.field == 'title')].message", "must not be blank"),
                             Arguments.of("about is empty", request, "$.errors[?(@.field == 'about')].message", "must not be blank"),
                             Arguments.of("price is empty", request, "$.errors[?(@.field == 'price')].message", "must not be null"),
                             Arguments.of("pages is empty", request, "$.errors[?(@.field == 'pages')].message", "must not be null"),
                             Arguments.of("isbn is empty", request, "$.errors[?(@.field == 'isbn')].message", "must not be blank"),
                             Arguments.of("published at is empty", request, "$.errors[?(@.field == 'publishedAt')].message", "must not be null"),
                             Arguments.of("category is empty", request, "$.errors[?(@.field == 'categoryId' && @.message == 'must not be null')].message", "must not be null"),
                             Arguments.of("author is empty", request, "$.errors[?(@.field == 'authorId' && @.message == 'must not be null')].message", "must not be null"),

                             Arguments.of("about is greather than 500 characters", requestWithLargeAbout(),
                                    "$.errors[?(@.field == 'about')].message", "size must be between 0 and 500"),
                             Arguments.of("price is smaller than 20", requestWithPriceSmallerThan20(),
                                    "$.errors[?(@.field == 'price')].message", "must be greater than or equal to 20"),
                             Arguments.of("pages is smaller than 100", requestWithPagesSmallerThan100(),
                                    "$.errors[?(@.field == 'pages')].message", "must be greater than or equal to 100"),
                             Arguments.of("published at is in the past", requestWithPublishedAtInThePast(),
                                    "$.errors[?(@.field == 'publishedAt')].message", "must be a future date"));
        }

        private Object requestWithPublishedAtInThePast() {
            LocalDate publishedAt = LocalDate.now().minusWeeks(2);

            return new CreateNewBookRequest(null, null, null,
                    null, null, null, publishedAt, null, null);
        }

        private CreateNewBookRequest requestWithPagesSmallerThan100() {
            int pages = 99;

            return new CreateNewBookRequest(null, null, null,
                    null, pages, null, null, null, null);
        }

        private CreateNewBookRequest requestWithPriceSmallerThan20() {
            BigDecimal price = BigDecimal.valueOf(19.99d);

            return new CreateNewBookRequest(null, null, null,
                    price, null, null, null, null, null);
        }

        private CreateNewBookRequest requestWithLargeAbout() {
            String about = generate(() -> "a").limit(600).collect(joining());

            return new CreateNewBookRequest(null, about, null,
                    null, null, null, null, null, null);
        }
    }

    static class WhenBookAlreadyExistsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(Arguments.of("title already exists", newBook(), "$.errors[?(@.field == 'title')].message", "already exists"),
                             Arguments.of("isbn already exists", newBook(), "$.errors[?(@.field == 'isbn')].message", "already exists"));
        }

        private Book newBook() {
            return new BookBuilder()
                    .withTitle("Code Complete")
                    .withAbout("This is a book about about Software Engineering...Etc etc etc...")
                    .withSummary("This is a book about about Software Engineering...Etc etc etc...")
                    .withPrice(BigDecimal.valueOf(99.99))
                    .withPages(567)
                    .withIsbn("123-4-56-78910-0")
                    .withPublishedAt(LocalDate.now().plusWeeks(2))
                    .withCategory(new Category("The Best Books of The World"))
                    .withAuthor(new Author("Fulano da Silva", "fulano@zup.com.br", "I'm Fulano, nice to meet you."))
                    .build();
        }
    }

    static class BookDependenciesArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            long categoryId = 1234l;
            long authorId = 1234l;

            CreateNewBookRequest request = new CreateNewBookRequest(null, null, null, null, null,
                    null, null, categoryId, authorId);

            return Stream.of(Arguments.of("category does not exist", request, "$.errors[?(@.field == 'categoryId')].message", "this value does not exist"),
                             Arguments.of("author does not exist", request, "$.errors[?(@.field == 'authorId')].message", "this value does not exist"));
        }
    }

    private String asJson(CreateNewBookRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}

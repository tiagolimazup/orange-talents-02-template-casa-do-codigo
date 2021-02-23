package br.com.zup.bootcamp.casadocodigo.book;

import br.com.zup.bootcamp.casadocodigo.author.Author;
import br.com.zup.bootcamp.casadocodigo.author.Authors;
import br.com.zup.bootcamp.casadocodigo.book.Book.BookBuilder;
import br.com.zup.bootcamp.casadocodigo.category.Categories;
import br.com.zup.bootcamp.casadocodigo.category.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
public class BookResourceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Authors authors;

    @Autowired
    Categories categories;

    @Autowired
    Books books;

    @Autowired
    ObjectMapper jsonMapper;

    Book book;

    @BeforeEach
    void before() {
        Category category = categories.save(new Category("Software Development"));
        Author author = authors.save(new Author("Tiago de Freitas Lima", "tiago.lima@zup.com.br", "I'm an author"));

        book = new BookBuilder()
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
    }

    @Test
    void listAllBooks() throws Exception {
        String expectedJson = jsonMapper.writeValueAsString(List.of(new BookResponse(book)));

        mockMvc.perform(get("/book"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}

package br.com.zup.bootcamp.casadocodigo.author;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
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
public class AuthorResourceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Authors authors;

    @Autowired
    private ObjectMapper jsonMapper;

    @Test
    void createNewAuthor() throws Exception {
        mockMvc.perform(post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewAuthorRequest("Tiago de Freitas Lima", "tiago.lima@zup.com.br", "I'm an author"))))
                .andExpect(status().isOk());

        assertTrue(authors.exists(Example.of(new Author("Tiago de Freitas Lima", "tiago.lima@zup.com.br", "I'm an author"))));
    }

    @Test
    void rejectNewAuthorWhenNameIsEmpty() throws Exception {
        mockMvc.perform(post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewAuthorRequest(null, "tiago.lima@zup.com.br", "I'm an author"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')]").exists());

        assertTrue(authors.count() == 0);
    }

    @Test
    void rejectNewAuthorWhenEmailIsEmpty() throws Exception {
        mockMvc.perform(post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewAuthorRequest("Tiago de Freitas Lima", null, "I'm an author"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')]").exists());

        assertTrue(authors.count() == 0);
    }

    @Test
    void rejectNewAuthorWhenDescriptionIsEmpty() throws Exception {
        mockMvc.perform(post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewAuthorRequest("Tiago de Freitas Lima", "tiago.lima@zup.com.br", null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'description')]").exists());

        assertTrue(authors.count() == 0);
    }

    @Test
    void rejectNewAuthorWhenDescriptionIsGreatherThen400Characters() throws Exception {
        String description = generate(() -> "a").limit(500).collect(joining());

        mockMvc.perform(post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewAuthorRequest("Tiago de Freitas Lima", "tiago.lima@zup.com.br", description))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'description')]").exists());

        assertTrue(authors.count() == 0);
    }

    private String asJson(CreateNewAuthorRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}

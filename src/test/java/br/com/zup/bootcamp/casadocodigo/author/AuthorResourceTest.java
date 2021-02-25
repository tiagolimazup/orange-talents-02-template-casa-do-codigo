package br.com.zup.bootcamp.casadocodigo.author;

import java.util.stream.Stream;

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
    ObjectMapper jsonMapper;

    @Test
    void createNewAuthor() throws Exception {
        mockMvc.perform(post("/author")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewAuthorRequest("Tiago de Freitas Lima", "tiago.lima@zup.com.br", "I'm an author"))))
                .andExpect(status().isOk());

        assertTrue(authors.exists(Example.of(new Author("Tiago de Freitas Lima", "tiago.lima@zup.com.br", "I'm an author"))));
    }

    @Nested
    class Restrictions {

        @ParameterizedTest(name = "[{index}] {0}")
        @ArgumentsSource(CreateNewAuthorRestrictionsArguments.class)
        @DisplayName("reject a new author when:")
        void rejectNewAuthorWhen(String title, CreateNewAuthorRequest request, String jsonPath, String expectedMessage) throws Exception {
            mockMvc.perform(post("/author")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(jsonPath).value(expectedMessage));

            assertTrue(authors.count() == 0);
        }

        @Nested
        class WhenAuthorAlreadyExists {

            @ParameterizedTest(name = "[{index}] {0}")
            @ArgumentsSource(WhenAuthorAlreadyExistsArguments.class)
            @DisplayName("when author already exists")
            void rejectNewAuthorWhen(String title, Author author, CreateNewAuthorRequest request, String jsonPath, String expectedMessage) throws Exception {
                authors.saveAndFlush(author);

                mockMvc.perform(post("/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath(jsonPath).value(expectedMessage));

                assertTrue(authors.count() == 1);
            }
        }
    }

    private String asJson(CreateNewAuthorRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }

    static class CreateNewAuthorRestrictionsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            CreateNewAuthorRequest request = new CreateNewAuthorRequest(null, null, null);

            return Stream.of(
                    Arguments.of("name is empty", request, "$.errors[?(@.field == 'name')].message", "must not be blank"),
                    Arguments.of("email is empty", request, "$.errors[?(@.field == 'email')].message", "must not be blank"),
                    Arguments.of("description is empty", request, "$.errors[?(@.field == 'description')].message", "must not be blank"),

                    Arguments.of("email is invalid", requestWithInvalidEmail(),
                            "$.errors[?(@.field == 'email')].message", "must be a well-formed email address"),
                    Arguments.of("description is greather than 400 characters", requestWithLargeDescription(),
                            "$.errors[?(@.field == 'description')].message", "size must be between 0 and 400"));
        }

        private CreateNewAuthorRequest requestWithInvalidEmail() {
            return new CreateNewAuthorRequest(null, "whatever@", null);
        }

        private CreateNewAuthorRequest requestWithLargeDescription() {
            String description = generate(() -> "a").limit(500).collect(joining());
            return new CreateNewAuthorRequest(null, null, description);
        }
    }

    static class WhenAuthorAlreadyExistsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            CreateNewAuthorRequest request = new CreateNewAuthorRequest("Tiago de Freitas Lima", "tiago.lima@zup.com.br", "I'm an author");

            Author author = request.toAuthor();

            return Stream.of(Arguments.of("email already exists", author, request, "$.errors[?(@.field == 'email')].message",
                    "already exists"));
        }
    }
}

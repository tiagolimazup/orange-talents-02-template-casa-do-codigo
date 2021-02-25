package br.com.zup.bootcamp.casadocodigo.category;

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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
public class CategoryResourceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Categories categories;

    @Autowired
    ObjectMapper jsonMapper;

    @Test
    void createNewCategory() throws Exception {
        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewCategoryRequest("Software Engineering"))))
                .andExpect(status().isOk());

        assertTrue(categories.exists(Example.of(new Category("Software Engineering"))));
    }

    @Nested
    class Restrictions {

        @ParameterizedTest(name = "[{index}] {0}")
        @ArgumentsSource(CreateNewCategoryRestrictionsArguments.class)
        @DisplayName("reject a new category when:")
        void rejectNewCategoryWhen(String title, CreateNewCategoryRequest request, String jsonPath, String expectedMessage) throws Exception {
            mockMvc.perform(post("/category")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(jsonPath).value(expectedMessage));

            assertTrue(categories.count() == 0);
        }

        @Nested
        class WhenCategoryAlreadyExists {

            @ParameterizedTest(name = "[{index}] {0}")
            @ArgumentsSource(WhenCategoryAlreadyExistsArguments.class)
            @DisplayName("when category already exists")
            void rejectNewCategoryWhen(String title, Category category, CreateNewCategoryRequest request, String jsonPath, String expectedMessage) throws Exception {
                categories.saveAndFlush(category);

                mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath(jsonPath).value(expectedMessage));

                assertTrue(categories.count() == 1);
            }
        }
    }

    static class CreateNewCategoryRestrictionsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            CreateNewCategoryRequest request = new CreateNewCategoryRequest(null);

            return Stream.of(Arguments.of("name is empty", request, "$.errors[?(@.field == 'name')].message", "must not be blank"));
        }
    }

    static class WhenCategoryAlreadyExistsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            Category category = new Category("Software Engineering");
            CreateNewCategoryRequest request = new CreateNewCategoryRequest(category.getName());

            return Stream.of(Arguments.of("name already exists", category, request, "$.errors[?(@.field == 'name')].message",
                    "already exists"));
        }
    }

    private String asJson(CreateNewCategoryRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}

package br.com.zup.bootcamp.casadocodigo.category;

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

    @Test
    void rejectNewCategoryWhenNameIsEmpty() throws Exception {
        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewCategoryRequest(null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')]").exists());

        assertTrue(categories.count() == 0);
    }

    @Test
    void rejectNewCategoryWhenNameAlreadyExists() throws Exception {
        Category category = new Category("Software Engineering");
        categories.save(category);

        mockMvc.perform(post("/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewCategoryRequest(category.getName()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')].message").value("already exists"));

        assertTrue(categories.count() == 1);
    }

    private String asJson(CreateNewCategoryRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}

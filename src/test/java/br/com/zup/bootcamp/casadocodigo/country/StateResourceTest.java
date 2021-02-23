package br.com.zup.bootcamp.casadocodigo.country;

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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Transactional
public class StateResourceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Countries countries;

    @Autowired
    ObjectMapper jsonMapper;

    Country country;

    @BeforeEach
    void before() {
        country = countries.save(new Country("BR", "Brazil"));
    }

    @Test
    void createNewState() throws Exception {
        mockMvc.perform(post("/country/{code}", country.getCode().toLowerCase())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewStateRequest("São Paulo"))))
                .andExpect(status().isOk());

        assertTrue(countries.findById(country.getCode()).map(c -> c.hasState("São Paulo")).orElse(false));
    }

    @Test
    void rejectNewStateWhenNameIsEmpty() throws Exception {
        mockMvc.perform(post("/country/{code}", country.getCode().toLowerCase())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewStateRequest(null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')]").exists());

        assertFalse(countries.findById(country.getCode()).map(c -> c.hasState("São Paulo")).orElse(false));
    }

    @Test
    void shouldReturnNotFoundWhenCountryDoesntExist() throws Exception {
        mockMvc.perform(post("/country/bla", country.getCode().toLowerCase())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewStateRequest("São Paulo"))))
                .andExpect(status().isNotFound());
    }

    private String asJson(CreateNewStateRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}
package br.com.zup.bootcamp.casadocodigo.country;

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
public class CountryResourceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Countries countries;

    @Autowired
    ObjectMapper jsonMapper;

    @Test
    void createNewCountry() throws Exception {
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewCountryRequest("BR", "Brazil"))))
                .andExpect(status().isOk());

        assertTrue(countries.exists(Example.of(new Country("BR", "Brazil"))));
    }

    @Test
    void rejectNewCountryWhenCodeIsEmpty() throws Exception {
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewCountryRequest(null, "Brazil"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'code')]").exists());

        assertTrue(countries.count() == 0);
    }

    @Test
    void rejectNewCountryWhenCodeIsGreatherThan2Characters() throws Exception {
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewCountryRequest("BRA", "Brazil"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'code')]").exists());
        ;

        assertTrue(countries.count() == 0);
    }

    @Test
    void rejectNewCountryWhenCodeAlreadyExists() throws Exception {
        Country country = countries.save(new Country("BR", "Brazil"));

        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewCountryRequest(country.getCode(), country.getName()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'code')]").exists());

        assertTrue(countries.count() == 1);
    }

    @Test
    void rejectNewCountryWhenNameIsEmpty() throws Exception {
        mockMvc.perform(post("/country")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJson(new CreateNewCountryRequest("BR", null))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'name')]").exists());
        ;

        assertTrue(countries.count() == 0);
    }

    private String asJson(CreateNewCountryRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}
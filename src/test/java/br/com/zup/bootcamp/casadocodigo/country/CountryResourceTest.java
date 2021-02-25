package br.com.zup.bootcamp.casadocodigo.country;

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

    @Nested
    class Restrictions {

        @ParameterizedTest(name = "[{index}] {0}")
        @ArgumentsSource(CreateNewCountryRestrictionsArguments.class)
        @DisplayName("reject a new country when:")
        void rejectNewCountryWhen(String title, CreateNewCountryRequest request, String jsonPath, String expectedMessage) throws Exception {
            mockMvc.perform(post("/country")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(jsonPath).value(expectedMessage));

            assertTrue(countries.count() == 0);
        }

        @Nested
        class WhenCountryAlreadyExists {

            @ParameterizedTest(name = "[{index}] {0}")
            @ArgumentsSource(WhenCountryAlreadyExistsArguments.class)
            @DisplayName("when author already exists")
            void rejectNewAuthorWhen(String title, Country country, CreateNewCountryRequest request, String jsonPath, String expectedMessage) throws Exception {
                countries.saveAndFlush(country);

                mockMvc.perform(post("/country")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath(jsonPath).value(expectedMessage));

                assertTrue(countries.count() == 1);
            }
        }
    }

    static class CreateNewCountryRestrictionsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            CreateNewCountryRequest request = new CreateNewCountryRequest(null, null);

            return Stream.of(
                    Arguments.of("code is empty", request, "$.errors[?(@.field == 'code')].message", "must not be blank"),
                    Arguments.of("name is empty", request, "$.errors[?(@.field == 'name')].message", "must not be blank"),

                    Arguments.of("code is greather than 2 characters", new CreateNewCountryRequest("BRA", null),
                            "$.errors[?(@.field == 'code')].message", "size must be between 2 and 2"));
        }
    }

    static class WhenCountryAlreadyExistsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            CreateNewCountryRequest request = new CreateNewCountryRequest("BR", "Brazil");

            Country country = request.toCountry();

            return Stream.of(Arguments.of("country code already exists", country, request, "$.errors[?(@.field == 'code')].message",
                    "already exists"));
        }
    }

    private String asJson(CreateNewCountryRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}
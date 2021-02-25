package br.com.zup.bootcamp.casadocodigo.customer;

import br.com.zup.bootcamp.casadocodigo.country.Countries;
import br.com.zup.bootcamp.casadocodigo.country.Country;

import java.util.List;
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
public class CustomerResourceTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    Customers customers;

    @Autowired
    Countries countries;

    @Autowired
    ObjectMapper jsonMapper;

    @BeforeEach
    void setup() {
        countries.save(new Country("BR", "Brazil", List.of("São Paulo")));
    }

    @Nested
    class CreateNewCustomer {

        @ParameterizedTest(name = "[{index}] {0}")
        @ArgumentsSource(CreateNewCustomerArguments.class)
        @DisplayName("create new customer:")
        void createNewCustomer(String title, CreateNewCustomerRequest request) throws Exception {
            mockMvc.perform(post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(request)))
                    .andExpect(status().isOk());

            assertTrue(customers.existsByEmail(request.getEmail()));
        }
    }

    @Nested
    class Restrictions {

        @ParameterizedTest(name = "[{index}] {0}")
        @ArgumentsSource(CreateNewCustomerRestrictionsArguments.class)
        @DisplayName("reject a new customer when:")
        void rejectNewCustomerWhen(String title, CreateNewCustomerRequest request, String jsonPath, String expectedMessage) throws Exception {
            mockMvc.perform(post("/customer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJson(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath(jsonPath).value(expectedMessage));

            assertTrue(customers.count() == 0);
        }

        @Nested
        class WhenCustomerAlreadyExists {

            @ParameterizedTest(name = "[{index}] {0}")
            @ArgumentsSource(WhenCustomerAlreadyExistsArguments.class)
            @DisplayName("when customer already exists")
            void rejectNewCustomerWhen(String title, CreateNewCustomerRequest request, String jsonPath, String expectedMessage) throws Exception {
                customers.saveAndFlush(request.toCustomer());

                mockMvc.perform(post("/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJson(request)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath(jsonPath).value(expectedMessage));

                assertTrue(customers.count() == 1);
            }
        }
    }

    static class CreateNewCustomerArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(
                    Arguments.of("with a valid CPF", createNewCustomerRequest("396.639.160-01")),
                    Arguments.of("with a valid CNPJ", createNewCustomerRequest("90.034.074/0001-31")));
        }

        CreateNewCustomerRequest createNewCustomerRequest(String document) {
            CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                    "São Paulo", "São Paulo", "BR", "12345-678");

            return new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                    "de Freitas Lima", document, address, "(11) 1234-5678");
        }
    }

    static class CreateNewCustomerRestrictionsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            CustomerAddressRequest address = new CustomerAddressRequest(null, null,
                    null, null, null, null);

            CreateNewCustomerRequest request = new CreateNewCustomerRequest(null, null,
                    null, null, address, null);

            return Stream.of(
                    Arguments.of("email is empty", request, "$.errors[?(@.field == 'email')].message", "must not be blank"),
                    Arguments.of("first name is empty", request, "$.errors[?(@.field == 'firstName')].message", "must not be blank"),
                    Arguments.of("last name is empty", request, "$.errors[?(@.field == 'lastName')].message", "must not be blank"),
                    Arguments.of("document is empty", request, "$.errors[?(@.field == 'document')].message", "must not be blank"),
                    Arguments.of("phone is empty", request, "$.errors[?(@.field == 'phone')].message", "must not be blank"),

                    Arguments.of("address street is empty", request, "$.errors[?(@.field == 'address.street')].message", "must not be blank"),
                    Arguments.of("address complement is empty", request, "$.errors[?(@.field == 'address.complement')].message", "must not be blank"),
                    Arguments.of("address city is empty", request, "$.errors[?(@.field == 'address.city')].message", "must not be blank"),
                    Arguments.of("address country is empty", request, "$.errors[?(@.field == 'address.country')].message", "must not be blank"),
                    Arguments.of("address postal code is empty", request, "$.errors[?(@.field == 'address.postalCode')].message", "must not be blank"),
                    Arguments.of("address state does not exist in the country", requestWithInvalidAddressState(),
                            "$.errors[?(@.field == 'address')].message", "state does not exist in the country"),

                    Arguments.of("email is invalid", requestWithInvalidEmail(),
                            "$.errors[?(@.field == 'email')].message", "must be a well-formed email address"),
                    Arguments.of("document (CPF) is invalid", requestWithInvalidDocument("123.456.789-00"),
                            "$.errors[?(@.field == 'document')].message", "invalid document"),
                    Arguments.of("document (CNPJ) is invalid", requestWithInvalidDocument("12.345.678/0001-00"),
                            "$.errors[?(@.field == 'document')].message", "invalid document"));
        }

        private CreateNewCustomerRequest requestWithInvalidAddressState() {
            CustomerAddressRequest address = new CustomerAddressRequest(null, null,
                    null, "Rio de Janeiro", null, null);

            return new CreateNewCustomerRequest(null, null,
                    null, null, address, null);
        }

        private Object requestWithInvalidDocument(String document) {
            return new CreateNewCustomerRequest(null, null,
                    null, document, null, null);
        }

        private CreateNewCustomerRequest requestWithInvalidEmail() {
            return new CreateNewCustomerRequest("whatever", null,
                    null, null, null, null);
        }
    }

    static class WhenCustomerAlreadyExistsArguments implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                    "São Paulo", "São Paulo", "BR", "12345-678");

            CreateNewCustomerRequest request = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                    "de Freitas Lima", "637.522.010-61", address, "(11) 1234-5678");

            return Stream.of(
                    Arguments.of("email already exists", request, "$.errors[?(@.field == 'email')].message", "already exists"),
                    Arguments.of("document already exists", request, "$.errors[?(@.field == 'document')].message", "already exists"));
        }
    }

    @Test
    void rejectNewCustomerWhenAddressStateDoesNotExistInTheCountry() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé", "Apto 456 A",
                "Rio de Janeiro", "Rio de Janeiro", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'address')].message").value("state does not exist in the country"));

        assertTrue(customers.count() == 0);
    }

    private String asJson(CreateNewCustomerRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}

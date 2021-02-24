package br.com.zup.bootcamp.casadocodigo.customer;

import br.com.zup.bootcamp.casadocodigo.country.Countries;
import br.com.zup.bootcamp.casadocodigo.country.Country;
import br.com.zup.bootcamp.casadocodigo.customer.Address.AddressBuilder;
import br.com.zup.bootcamp.casadocodigo.customer.Customer.CustomerBuilder;

import java.util.List;

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

    @Test
    void createNewCustomer() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertTrue(customers.existsByEmail("tiago.lima@zup.com.br"));
    }

    @Test
    void rejectNewCustomerWhenEmailIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest(null, "Tiago",
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')]").exists());

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenEmailIsInvalid() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("whatever", "Tiago",
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')]").exists());

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenEmailAlreadyExists() throws Exception {
        Address address = new AddressBuilder()
                .withStreet("Praça da Sé, 123")
                .withComplement("Apto 456 A")
                .withCity("São Paulo")
                .withState("São Paulo")
                .withCountry("BR")
                .withPostalCode("12345-678")
                .build();

        Customer customer = customers.save(new CustomerBuilder()
                .withEmail("tiago.lima@zup.com.br")
                .withName("Tiago", "de Freitas Lima")
                .withDocument("015.450.640-08")
                .withAddress(address)
                .withPhone("(11) 1234-5678")
                .build());

        CreateNewCustomerRequest createNewCustomerRequest = CreateNewCustomerRequest.from(customer);

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'email')].message").value("already exists"));

        assertTrue(customers.count() == 1);
    }

    @Test
    void rejectNewCustomerWhenFirstNameIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", null,
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'firstName')]").exists());

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenLastNameIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                null, "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'lastName')]").exists());

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenDocumentIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", null, address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'document')]").exists());

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenDocumentIsAnInvalidCPF() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "123.456.789-00", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'document')].message").value("invalid document"));

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenDocumentIsAnInvalidCNPJ() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "12.345.678/0001-00", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'document')].message").value("invalid document"));

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenDocumentAlreadyExists() throws Exception {
        Address address = new AddressBuilder()
                .withStreet("Praça da Sé, 123")
                .withComplement("Apto 456 A")
                .withCity("São Paulo")
                .withState("São Paulo")
                .withCountry("BR")
                .withPostalCode("12345-678")
                .build();

        Customer customer = customers.save(new CustomerBuilder()
                .withEmail("tiago.lima@zup.com.br")
                .withName("Tiago", "de Freitas Lima")
                .withDocument("015.450.640-08")
                .withAddress(address)
                .withPhone("(11) 1234-5678")
                .build());

        CreateNewCustomerRequest createNewCustomerRequest = CreateNewCustomerRequest.from(customer);

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'document')].message").value("already exists"));

        assertTrue(customers.count() == 1);
    }

    @Test
    void rejectNewCustomerWhenAddressStreetIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest(null, "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'address.street')]").exists());

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenAddressComplementIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé", null,
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'address.complement')]").exists());

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenAddressCityIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé", "Apto 456 A",
                null, "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'address.city')]").exists());

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenAddressCountryIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé", "Apto 456 A",
                "São Paulo", "São Paulo", null, "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'address.country')]").exists());

        assertTrue(customers.count() == 0);
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

    @Test
    void rejectNewCustomerWhenAddressPostalCodeIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", null);

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "015.450.640-08", address, "(11) 1234-5678");

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'address.postalCode')]").exists());

        assertTrue(customers.count() == 0);
    }

    @Test
    void rejectNewCustomerWhenPhoneIsEmpty() throws Exception {
        CustomerAddressRequest address = new CustomerAddressRequest("Praça da Sé", "Apto 456 A",
                "São Paulo", "São Paulo", "BR", "12345-678");

        CreateNewCustomerRequest createNewCustomerRequest = new CreateNewCustomerRequest("tiago.lima@zup.com.br", "Tiago",
                "de Freitas Lima", "015.450.640-08", address, null);

        mockMvc.perform(post("/customer")
                .content(asJson(createNewCustomerRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field == 'phone')]").exists());

        assertTrue(customers.count() == 0);
    }

    private String asJson(CreateNewCustomerRequest request) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(request);
    }
}

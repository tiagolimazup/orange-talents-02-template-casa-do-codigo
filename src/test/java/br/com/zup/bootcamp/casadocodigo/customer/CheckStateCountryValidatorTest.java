package br.com.zup.bootcamp.casadocodigo.customer;

import br.com.zup.bootcamp.casadocodigo.country.Countries;
import br.com.zup.bootcamp.casadocodigo.country.Country;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckStateCountryValidatorTest {

    @Mock
    Countries countries;

    @InjectMocks
    CheckStateCountryValidator validator;

    @Test
    void shouldReturnValidWhenCountryDoesNotHaveAnyStates() {
        CustomerAddressRequest address = addressWithState(null);

        when(countries.findById("BR")).thenReturn(Optional.of(countryWithStates()));

        assertTrue(validator.isValid(address, null));
    }

    @Test
    void shouldReturnValidWhenCountryHasTheState() {
        CustomerAddressRequest address = addressWithState("São Paulo");

        when(countries.findById("BR")).thenReturn(Optional.of(countryWithStates("São Paulo")));

        assertTrue(validator.isValid(address, null));
    }

    @Test
    void shouldReturnInvalidWhenCountryDoesNotHaveTheState() {
        CustomerAddressRequest address = addressWithState("São Paulo");

        when(countries.findById("BR")).thenReturn(Optional.of(countryWithStates("Rio de Janeiro")));

        assertFalse(validator.isValid(address, null));
    }

    @Test
    void shouldReturnInvalidWhenStateIsNullButCountryHasStates() {
        CustomerAddressRequest address = addressWithState(null);

        when(countries.findById("BR")).thenReturn(Optional.of(countryWithStates("São Paulo")));

        assertFalse(validator.isValid(address, null));
    }

    @Test
    void shouldReturnValidWhenStateIsNullAndCountryDoestNotHaveStates() {
        CustomerAddressRequest address = addressWithState(null);

        when(countries.findById("BR")).thenReturn(Optional.of(countryWithStates()));

        assertTrue(validator.isValid(address, null));
    }

    private Country countryWithStates(String... state) {
        return new Country("BR", "Brazil", List.of(state));
    }

    private CustomerAddressRequest addressWithState(String state) {
        return new CustomerAddressRequest("Praça da Sé, 123", "Apto 456 A",
                "São Paulo", state, "BR", "12345-678");
    }
}
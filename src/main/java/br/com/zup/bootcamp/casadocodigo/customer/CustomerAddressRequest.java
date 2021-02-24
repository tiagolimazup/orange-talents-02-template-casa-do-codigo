package br.com.zup.bootcamp.casadocodigo.customer;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

@CheckStateCountry
class CustomerAddressRequest {

    @JsonProperty
    @NotBlank
    private final String street;

    @JsonProperty
    @NotBlank
    private final String complement;

    @JsonProperty
    @NotBlank
    private final String city;

    @JsonProperty
    private final String state;

    @JsonProperty
    @NotBlank
    private final String country;

    @JsonProperty
    @NotBlank
    private final String postalCode;

    CustomerAddressRequest(String street, String complement, String city, String state, String country, String postalCode) {
        this.street = street;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public String getComplement() {
        return complement;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    static CustomerAddressRequest from(Address address) {
        return new CustomerAddressRequest(address.getStreet(), address.getComplement(), address.getCity(),
                address.getState(), address.getCountry(), address.getPostalCode());
    }

    Address newAddress() {
        return new Address.AddressBuilder()
                .withStreet(street)
                .withComplement(complement)
                .withCity(city)
                .withState(state)
                .withCountry(country)
                .withPostalCode(postalCode)
                .build();
    }
}

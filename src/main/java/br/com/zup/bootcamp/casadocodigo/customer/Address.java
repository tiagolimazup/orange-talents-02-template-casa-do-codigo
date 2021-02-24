package br.com.zup.bootcamp.casadocodigo.customer;

import javax.persistence.Embeddable;

@Embeddable
class Address {

    private String street;

    private String complement;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    @Deprecated
    Address() {
    }

    private Address(String street, String complement, String city, String state, String country, String postalCode) {
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

    static class AddressBuilder {

        private String street;

        private String complement;

        private String city;

        private String state;

        private String country;

        private String postalCode;

        AddressBuilder withStreet(String street) {
            this.street = street;
            return this;
        }

        AddressBuilder withComplement(String complement) {
            this.complement = complement;
            return this;
        }

        AddressBuilder withCity(String city) {
            this.city = city;
            return this;
        }

        AddressBuilder withState(String state) {
            this.state = state;
            return this;
        }

        AddressBuilder withCountry(String country) {
            this.country = country;
            return this;
        }

        AddressBuilder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        Address build() {
            return new Address(street, complement, city, state, country, postalCode);
        }
    }
}

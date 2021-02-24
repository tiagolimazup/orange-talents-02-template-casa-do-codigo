package br.com.zup.bootcamp.casadocodigo.customer;

import br.com.zup.bootcamp.casadocodigo.validation.UniqueValue;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

class CreateNewCustomerRequest {

    @JsonProperty
    @NotBlank
    @Email
    @UniqueValue(entity = Customer.class, field = "email")
    final String email;

    @JsonProperty
    @NotBlank
    final String firstName;

    @JsonProperty
    @NotBlank
    final String lastName;

    @JsonProperty
    @NotBlank
    @CPFOrCNPJ
    @UniqueValue(entity = Customer.class, field = "document")
    final String document;

    @JsonProperty
    @Valid
    final CustomerAddressRequest address;

    @JsonProperty
    @NotBlank
    final String phone;

    CreateNewCustomerRequest(String email, String firstName, String lastName, String document, CustomerAddressRequest address, String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.address = address;
        this.phone = phone;
    }

    public CustomerAddressRequest getAddress() {
        return address;
    }

    static CreateNewCustomerRequest from(Customer customer) {
        return new CreateNewCustomerRequest(customer.getEmail(), customer.getFirstName(), customer.getLastName(),
                customer.getDocument(), CustomerAddressRequest.from(customer.getAddress()),
                customer.getPhone());
    }

    Customer newCustomer() {
        return new Customer.CustomerBuilder()
                .withEmail(email)
                .withName(firstName, lastName)
                .withDocument(document)
                .withAddress(address.newAddress())
                .withPhone(phone)
                .build();
    }
}

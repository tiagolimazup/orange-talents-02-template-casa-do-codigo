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
    private final String email;

    @JsonProperty
    @NotBlank
    private final String firstName;

    @JsonProperty
    @NotBlank
    private final String lastName;

    @JsonProperty
    @NotBlank
    @CPFOrCNPJ
    @UniqueValue(entity = Customer.class, field = "document")
    private final String document;

    @JsonProperty
    @Valid
    private final CustomerAddressRequest address;

    @JsonProperty
    @NotBlank
    private final String phone;

    CreateNewCustomerRequest(String email, String firstName, String lastName, String document, CustomerAddressRequest address, String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.address = address;
        this.phone = phone;
    }

    String getEmail() {
        return email;
    }

    public CustomerAddressRequest getAddress() {
        return address;
    }

    static CreateNewCustomerRequest from(Customer customer) {
        return new CreateNewCustomerRequest(customer.getEmail(), customer.getFirstName(), customer.getLastName(),
                customer.getDocument(), CustomerAddressRequest.from(customer.getAddress()),
                customer.getPhone());
    }

    Customer toCustomer() {
        return new Customer.CustomerBuilder()
                .withEmail(email)
                .withName(firstName, lastName)
                .withDocument(document)
                .withAddress(address.newAddress())
                .withPhone(phone)
                .build();
    }
}

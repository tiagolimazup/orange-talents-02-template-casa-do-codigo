package br.com.zup.bootcamp.casadocodigo.customer;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String firstName;

    private String lastName;

    private String document;

    @Embedded
    private Address address;

    private String phone;

    @Deprecated
    Customer() {
    }

    private Customer(String email, String firstName, String lastName, String document, Address address, String phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.document = document;
        this.address = address;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocument() {
        return document;
    }

    public Address getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    static class CustomerBuilder {

        private String email;

        private String firstName;

        private String lastName;

        private String document;

        private Address address;

        private String phone;

        CustomerBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        CustomerBuilder withName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            return this;
        }

        CustomerBuilder withDocument(String document) {
            this.document = document;
            return this;
        }

        CustomerBuilder withAddress(Address address) {
            this.address = address;
            return this;
        }

        CustomerBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        Customer build() {
            return new Customer(email, firstName, lastName, document, address, phone);
        }
    }
}

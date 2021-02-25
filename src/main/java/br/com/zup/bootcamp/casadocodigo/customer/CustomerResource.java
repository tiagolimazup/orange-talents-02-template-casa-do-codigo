package br.com.zup.bootcamp.casadocodigo.customer;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
class CustomerResource {

    final Customers customers;

    CustomerResource(Customers customers) {
        this.customers = customers;
    }

    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody CreateNewCustomerRequest request) {
        customers.save(request.toCustomer());
        return ResponseEntity.ok().build();
    }
}

package br.com.zup.bootcamp.casadocodigo.customer;

import org.springframework.data.jpa.repository.JpaRepository;

interface Customers extends JpaRepository<Customer, Long> {

    boolean existsByEmail(String email);
}

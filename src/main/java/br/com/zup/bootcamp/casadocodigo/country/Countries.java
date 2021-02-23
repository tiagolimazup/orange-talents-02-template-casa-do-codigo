package br.com.zup.bootcamp.casadocodigo.country;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Countries extends JpaRepository<Country, String> {
}

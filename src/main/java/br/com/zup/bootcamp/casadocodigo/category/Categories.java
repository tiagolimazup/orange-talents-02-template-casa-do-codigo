package br.com.zup.bootcamp.casadocodigo.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Categories extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}

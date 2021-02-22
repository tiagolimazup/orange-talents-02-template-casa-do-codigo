package br.com.zup.bootcamp.casadocodigo.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface Authors extends JpaRepository<Author, Long> {
}

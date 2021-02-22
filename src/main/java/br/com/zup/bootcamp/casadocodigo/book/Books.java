package br.com.zup.bootcamp.casadocodigo.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Books extends JpaRepository<Book, Long> {

    boolean existsByTitle(String title);
}

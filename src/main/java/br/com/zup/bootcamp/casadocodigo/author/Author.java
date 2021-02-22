package br.com.zup.bootcamp.casadocodigo.author;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    String email;

    String description;

    @Deprecated
    Author() {
    }

    Author(String name, String email, String description) {
        this.name = name;
        this.email = email;
        this.description = description;
    }
}

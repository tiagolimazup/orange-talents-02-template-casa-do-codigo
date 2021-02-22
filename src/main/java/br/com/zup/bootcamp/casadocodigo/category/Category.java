package br.com.zup.bootcamp.casadocodigo.category;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    @Deprecated
    Category() {
    }

    Category(String name) {
        this.name = name;
    }
}

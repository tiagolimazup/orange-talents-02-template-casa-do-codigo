package br.com.zup.bootcamp.casadocodigo.country;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;

@Entity
class Country {

    @Id
    String code;

    String name;

    @ElementCollection
    Set<String> states = new HashSet<>();

    @Deprecated
    Country() {
    }

    Country(String code, String name) {
        this.code = code;
        this.name = name;
        this.states = new HashSet<>();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean hasState(String state) {
        return states.contains(state);
    }

    public Country addState(String state) {
        states.add(state);
        return this;
    }
}

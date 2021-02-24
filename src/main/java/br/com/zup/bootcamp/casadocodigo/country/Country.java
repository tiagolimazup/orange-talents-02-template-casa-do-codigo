package br.com.zup.bootcamp.casadocodigo.country;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.emptySet;

@Entity
public class Country {

    @Id
    String code;

    String name;

    @ElementCollection
    Set<String> states = new HashSet<>();

    @Deprecated
    Country() {
    }

    public Country(String code, String name) {
        this(code, name, emptySet());
    }

    public Country(String code, String name, Collection<String> states) {
        this.code = code;
        this.name = name;
        this.states = new HashSet<>(states);
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public boolean hasState(String state) {
        return state != null && states.contains(state);
    }

    public Country addState(String state) {
        states.add(state);
        return this;
    }
}

package br.com.zup.bootcamp.casadocodigo.country;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/country/{code}")
class StateResource {

    final Countries countries;

    StateResource(Countries countries) {
        this.countries = countries;
    }

    @PostMapping
    ResponseEntity<?> create(@PathVariable String code, @Valid @RequestBody CreateNewStateRequest request) {
        return countries.findById(code.toUpperCase())
                .map(country -> country.addState(request.name))
                .map(countries::save)
                .map(country -> ResponseEntity.ok().build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

package br.com.zup.bootcamp.casadocodigo.country;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/country")
class CountryResource {

    final Countries countries;

    CountryResource(Countries countries) {
        this.countries = countries;
    }

    @PostMapping
    ResponseEntity<Void> create(@Valid @RequestBody CreateNewCountryRequest request) {
        countries.save(request.newCountry());
        return ResponseEntity.ok().build();
    }
}

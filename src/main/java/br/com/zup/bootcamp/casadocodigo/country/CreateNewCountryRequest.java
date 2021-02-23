package br.com.zup.bootcamp.casadocodigo.country;

import br.com.zup.bootcamp.casadocodigo.validation.UniqueValue;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class CreateNewCountryRequest {

    @JsonProperty
    @NotBlank
    @Size(min = 2, max = 2)
    @UniqueValue(entity = Country.class, field = "code")
    final String code;

    @JsonProperty
    @NotBlank
    final String name;

    @JsonCreator
    CreateNewCountryRequest(String code, String name) {
        this.code = code;
        this.name = name;
    }

    Country newCountry() {
        return new Country(code.toUpperCase(), name);
    }
}

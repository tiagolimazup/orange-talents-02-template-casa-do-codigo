package br.com.zup.bootcamp.casadocodigo.country;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class CreateNewStateRequest {

    @JsonProperty
    @NotBlank
    final String name;

    @JsonCreator
    CreateNewStateRequest(String name) {
        this.name = name;
    }
}

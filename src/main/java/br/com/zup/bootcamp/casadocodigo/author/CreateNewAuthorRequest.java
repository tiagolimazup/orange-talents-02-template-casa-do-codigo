package br.com.zup.bootcamp.casadocodigo.author;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class CreateNewAuthorRequest {

    @JsonProperty
    @NotBlank
    final String name;

    @JsonProperty
    @NotBlank
    @UniqueEmail
    final String email;

    @JsonProperty
    @NotBlank
    @Size(max = 400)
    final String description;

    @JsonCreator
    CreateNewAuthorRequest(String name, String email, String description) {
        this.name = name;
        this.email = email;
        this.description = description;
    }

    public Author newAuthor() {
        return new Author(name, email, description);
    }
}

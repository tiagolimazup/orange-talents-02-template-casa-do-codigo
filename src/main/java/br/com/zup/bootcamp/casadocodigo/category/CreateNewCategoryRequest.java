package br.com.zup.bootcamp.casadocodigo.category;

import br.com.zup.bootcamp.casadocodigo.validation.UniqueValue;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

class CreateNewCategoryRequest {

    @JsonProperty
    @NotBlank
    @UniqueValue(entity = Category.class, field = "name")
    private final String name;

    @JsonCreator
    CreateNewCategoryRequest(String name) {
        this.name = name;
    }

    Category newCategory() {
        return new Category(name);
    }
}

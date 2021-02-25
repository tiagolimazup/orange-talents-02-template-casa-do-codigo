package br.com.zup.bootcamp.casadocodigo.category;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
class CategoryResource {

    final Categories categories;

    CategoryResource(Categories categories) {
        this.categories = categories;
    }

    @PostMapping
    ResponseEntity<Void> createNewCategory(@Valid @RequestBody CreateNewCategoryRequest request) {
        categories.save(request.toCategory());
        return ResponseEntity.ok().build();
    }
}

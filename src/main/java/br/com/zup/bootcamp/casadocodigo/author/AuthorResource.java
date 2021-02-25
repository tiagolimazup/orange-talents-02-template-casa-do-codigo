package br.com.zup.bootcamp.casadocodigo.author;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/author")
class AuthorResource {

    final Authors authors;

    AuthorResource(Authors authors) {
        this.authors = authors;
    }

    @PostMapping
    ResponseEntity<Void> createNewAuthor(@Valid @RequestBody CreateNewAuthorRequest request) {
        authors.save(request.toAuthor());
        return ok().build();
    }
}

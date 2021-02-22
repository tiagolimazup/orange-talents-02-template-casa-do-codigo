package br.com.zup.bootcamp.casadocodigo.author;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    final Authors authors;

    public UniqueEmailValidator(Authors authors) {
        this.authors = authors;
    }

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !authors.existsByEmail(value);
    }
}

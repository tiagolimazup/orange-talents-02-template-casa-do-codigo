package br.com.zup.bootcamp.casadocodigo.category;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

    final Categories categories;

    public UniqueNameValidator(Categories categories) {
        this.categories = categories;
    }

    @Override
    public void initialize(UniqueName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return !categories.existsByName(value);
    }
}

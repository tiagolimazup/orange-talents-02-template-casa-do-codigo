package br.com.zup.bootcamp.casadocodigo.customer;

import br.com.zup.bootcamp.casadocodigo.country.Countries;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

class CheckStateCountryValidator implements ConstraintValidator<CheckStateCountry, CustomerAddressRequest> {

    final Countries countries;

    CheckStateCountryValidator(Countries countries) {
        this.countries = countries;
    }

    @Override
    public void initialize(CheckStateCountry constraintAnnotation) {
    }

    @Override
    public boolean isValid(CustomerAddressRequest request, ConstraintValidatorContext context) {
        return Optional.ofNullable(request.getCountry())
                .flatMap(countries::findById)
                .map(country -> (request.getState() != null && country.hasState(request.getState())) || !country.hasStates())
                .orElse(false);
    }
}

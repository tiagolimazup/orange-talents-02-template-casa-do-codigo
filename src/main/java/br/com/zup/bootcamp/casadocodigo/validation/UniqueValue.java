package br.com.zup.bootcamp.casadocodigo.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueValueValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueValue {

    Class<?> entity();

    String field();

    String message() default "already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

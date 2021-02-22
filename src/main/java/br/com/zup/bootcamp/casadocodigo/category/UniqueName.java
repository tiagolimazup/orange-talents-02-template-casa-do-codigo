package br.com.zup.bootcamp.casadocodigo.category;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueNameValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@interface UniqueName {

    String message() default "name already exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package br.com.zup.bootcamp.casadocodigo.customer;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ConstraintComposition(CompositionType.OR)
@ReportAsSingleViolation
@CPF
@CNPJ
@Constraint(validatedBy = {})
public @interface CPFOrCNPJ {

    String message() default "invalid document";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

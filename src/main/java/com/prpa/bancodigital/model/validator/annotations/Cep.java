package com.prpa.bancodigital.model.validator.annotations;

import com.prpa.bancodigital.model.validator.CepValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = CepValidator.class)
public @interface Cep {

    String message() default "Cep inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}

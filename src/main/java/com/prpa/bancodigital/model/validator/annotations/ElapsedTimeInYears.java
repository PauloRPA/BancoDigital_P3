package com.prpa.bancodigital.model.validator.annotations;

import com.prpa.bancodigital.model.validator.ElapsedTimeUntilNowValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ElapsedTimeUntilNowValidator.class)
public @interface ElapsedTimeInYears {

    String message() default "Data invalida";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int greater() default Integer.MIN_VALUE;

    int lesser() default Integer.MAX_VALUE;

}

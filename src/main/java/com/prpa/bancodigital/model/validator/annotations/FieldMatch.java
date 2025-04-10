package com.prpa.bancodigital.model.validator.annotations;

import java.lang.annotation.Documented;

import com.prpa.bancodigital.model.validator.FieldMatchConstraintValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = FieldMatchConstraintValidation.class)
public @interface FieldMatch {

    String fieldName();

    String confirmFieldName();

    String message() default "asdf";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Target(TYPE)
    @Retention(RUNTIME)
    public @interface List {

        FieldMatch[] value();

    }

}

package com.prpa.bancodigital.model.validator.annotations;

import java.lang.annotation.Documented;

import com.prpa.bancodigital.model.validator.FieldMatchConstraintValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchConstraintValidation.class)
public @interface FieldMatch {

    String fieldName();

    String confirmFieldName();

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Documented
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface List {

        FieldMatch[] value();

    }

}

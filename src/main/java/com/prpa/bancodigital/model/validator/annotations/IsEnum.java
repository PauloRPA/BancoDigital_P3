package com.prpa.bancodigital.model.validator.annotations;

import com.prpa.bancodigital.model.validator.isEnumConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = isEnumConstraintValidator.class)
public @interface IsEnum {

    Class<? extends Enum<?>> type();

    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

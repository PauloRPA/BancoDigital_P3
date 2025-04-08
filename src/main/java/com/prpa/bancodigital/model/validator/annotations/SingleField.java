package com.prpa.bancodigital.model.validator.annotations;

import com.prpa.bancodigital.model.validator.CepValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.hibernate.tool.schema.TargetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.PARAMETER})
@Retention(RUNTIME)
public @interface SingleField {

    String name();

}

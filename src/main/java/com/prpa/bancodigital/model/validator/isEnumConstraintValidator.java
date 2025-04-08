package com.prpa.bancodigital.model.validator;

import com.prpa.bancodigital.model.validator.annotations.IsEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class isEnumConstraintValidator implements ConstraintValidator<IsEnum, String> {

    private List<String> enumNames;
    public final String unit = "sdf";

    @Override
    public void initialize(IsEnum annotation) {
        enumNames = Stream.of(annotation.type().getEnumConstants())
                .map(Enum::name)
                .toList();
        ConstraintValidator.super.initialize(annotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        return enumNames.contains(value.toUpperCase());
    }

}

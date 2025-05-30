package com.prpa.bancodigital.model.validator;

import com.prpa.bancodigital.exception.GettersNotFoundForMatchingFields;
import com.prpa.bancodigital.model.validator.annotations.FieldMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.lang.Character.toUpperCase;

@Slf4j
public class FieldMatchConstraintValidation implements ConstraintValidator<FieldMatch, Object> {

    private String getterFieldName;
    private String getterConfirmFieldName;
    private String fieldName;
    private String confirmFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        fieldName = constraintAnnotation.fieldName();
        confirmFieldName = constraintAnnotation.confirmFieldName();

        getterFieldName = "get" + toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        getterConfirmFieldName = "get" + toUpperCase(confirmFieldName.charAt(0)) + confirmFieldName.substring(1);

        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object toBeValidated, ConstraintValidatorContext context) {
        List<Method> getterMethods = Arrays.stream(toBeValidated.getClass().getDeclaredMethods())
                .filter(method -> method.getName().equals(getterFieldName) ||
                        method.getName().equals(getterConfirmFieldName))
                .toList();

        if (getterMethods.size() < 2) {
            String msg = "No getters found for fields: %s and %s. The getter methods are %s and %s.";
            throw new GettersNotFoundForMatchingFields(String.format(msg,
                    fieldName, confirmFieldName, getterFieldName, getterConfirmFieldName));
        }

        try {
            Object fieldValue = getterMethods.get(0).invoke(toBeValidated);
            Object confirmFieldValue = getterMethods.get(1).invoke(toBeValidated);
            if (fieldValue == null || confirmFieldValue == null)
                return false;
            return fieldValue.equals(confirmFieldValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Não foi possível invocar os getters para verificar se os campos de senha são iguais");
            return false;
        }
    }

}

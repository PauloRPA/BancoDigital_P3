package com.prpa.bancodigital.model.validator;

import com.prpa.bancodigital.model.validator.annotations.Cep;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Component
public class CepValidator implements ConstraintValidator<Cep, String> {

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null) return true;
        if (cep.isBlank()) return false;
        if (cep.length() == 9) {
            if (cep.charAt(5) != '-')
                return false;
            cep = cep.replaceFirst("-", "");
        }
        if (cep.length() != 8)
            return false;

        String nonDigits = cep.chars()
                .mapToObj(ch -> (char) ch)
                .filter(not(Character::isDigit))
                .map(String::valueOf)
                .collect(Collectors.joining());

        if (!nonDigits.isBlank())
            return false;

        return true;
    }

}

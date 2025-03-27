package com.prpa.bancodigital.model.validator;

import com.prpa.bancodigital.model.validator.annotations.ElapsedTimeInYears;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class ElapsedTimeUntilNowValidator implements ConstraintValidator<ElapsedTimeInYears, LocalDate> {

    private int greaterInYears;
    private int lesserInYears;

    @Override
    public void initialize(ElapsedTimeInYears constraintAnnotation) {
        greaterInYears = constraintAnnotation.greater();
        lesserInYears = constraintAnnotation.lesser();
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null)
            return true;
        Period until = value.until(LocalDate.now());
        return until.getYears() > greaterInYears && until.getYears() < lesserInYears;
    }

}

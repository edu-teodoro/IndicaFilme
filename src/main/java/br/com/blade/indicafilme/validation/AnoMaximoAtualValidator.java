package br.com.blade.indicafilme.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Year;

public class AnoMaximoAtualValidator implements ConstraintValidator<AnoMaximoAtual, Integer> {
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) return true;
        int anoAtual = Year.now().getValue();
        return value <= anoAtual;
    }
}

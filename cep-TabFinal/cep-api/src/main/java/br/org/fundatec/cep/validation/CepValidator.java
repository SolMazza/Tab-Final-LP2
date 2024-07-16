package br.org.fundatec.cep.validation;

import br.org.fundatec.cep.annotation.CepValidation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class CepValidator implements ConstraintValidator<CepValidation, Integer> {

    @Override
    public void initialize(final CepValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(Integer cep, final ConstraintValidatorContext context) {
        boolean result = false;
        if ( cep == null || cep.compareTo(0) == 0) {
            result = false;
        } else {
            result = cep.compareTo(90000000) >= 0 && cep.compareTo(100000000) <= 0 ? true : false;
        }
        return result;
    }
}
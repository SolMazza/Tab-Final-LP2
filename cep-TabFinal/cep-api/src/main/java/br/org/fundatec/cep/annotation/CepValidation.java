package br.org.fundatec.cep.annotation;


import br.org.fundatec.cep.validation.CepValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CepValidator.class)
public @interface CepValidation {

    Class<?>[] groups() default {};

    String message() default "Cep inválido";

    Class<? extends Payload>[] payload() default {};

}

package br.com.blade.indicafilme.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = AnoMaximoAtualValidator.class)
@Target({ FIELD })
@Retention(RUNTIME)
public @interface AnoMaximoAtual {
    String message() default "O ano de lançamento deve ser no máximo o ano atual";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

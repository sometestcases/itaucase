package testcase.api.validators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import testcase.api.exceptionHandle.MessageCodes;

@Constraint(validatedBy = NotZeroValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotZero {
    String message() default MessageCodes.NOT_ZERO_VALUE;

    Class<?>[] groups() default {};

    Class<Payload>[] payload() default {};
}

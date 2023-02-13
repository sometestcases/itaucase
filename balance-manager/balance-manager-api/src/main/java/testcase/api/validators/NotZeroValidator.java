package testcase.api.validators;

import java.math.BigDecimal;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import testcase.api.requests.SingleOperationRequest;

public class NotZeroValidator implements ConstraintValidator<NotZero, BigDecimal> {
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return !value.equals(BigDecimal.ZERO);
    }
}

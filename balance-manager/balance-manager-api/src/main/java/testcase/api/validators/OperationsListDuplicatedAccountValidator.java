package testcase.api.validators;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import testcase.api.requests.SingleOperationRequest;

public class OperationsListDuplicatedAccountValidator implements ConstraintValidator<NotDuplicatedAccounts, List<SingleOperationRequest>> {
    @Override
    public boolean isValid(List<SingleOperationRequest> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return value.stream().map(SingleOperationRequest::getAccountId).distinct().count() == value.size();
    }
}

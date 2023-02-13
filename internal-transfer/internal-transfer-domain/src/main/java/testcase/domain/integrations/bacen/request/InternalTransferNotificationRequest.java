package testcase.domain.integrations.bacen.request;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import testcase.domain.integrations.balanceManager.request.SingleOperationRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalTransferNotificationRequest {

    @JsonProperty("unique_identifier")
    private String uniqueIdentifier;

    @JsonProperty("debtor_account_agency")
    private int debtorAccountAgency;

    @JsonProperty("debtor_account_number")
    private int debtorAccountNumber;

    @JsonProperty("creditor_account_agency")
    private int creditorAccountAgency;

    @JsonProperty("creditor_account_number")
    private int creditorAccountNumber;

    private BigDecimal value;
}

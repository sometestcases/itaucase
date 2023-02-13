package testcase.domain.service.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import testcase.domain.service.model.AccountState;
import testcase.event.avro.BalanceUpdate;

@Component
public class AccountStateMapper {

    @Value("${date.zone}")
    private String dateZone;

    public BalanceUpdate toBalanceUpdateEvent(AccountState st, LocalDateTime updateDate) {
        return BalanceUpdate.newBuilder()
                .setAccountId(st.getAccountId())
                .setNumber(st.getNumber())
                .setAgency(st.getAgency())
                .setBalance(st.getBalance().doubleValue())
                .setBlocked(st.isBlocked())
                .setUpdateDate(updateDate
                        .atZone(ZoneId.of(this.dateZone)).toInstant().toEpochMilli())
                .setUpdateOrder(st.getOperationOrder())
                .build();
    }
}

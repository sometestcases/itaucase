package testcase.domain.mapper;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import testcase.domain.model.AccountStatus;
import testcase.event.avro.AccountCreated;
import testcase.persistence.entities.Account;

@Component
public class AccountMapper {

    @Value("${date.zone}")
    private String dateZone;

    public testcase.domain.model.Account map(Account entity) {

        return testcase.domain.model.Account.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .customerId(entity.getCustomerId())
                .number(entity.getNumber())
                .agency(entity.getAgency().getNumber())
                .creationDate(entity.getCreationDate())
                .status(AccountStatus.valueOf(entity.getStatus().name()))
                .updateDate(entity.getUpdateDate())
                .build();
    }

    public AccountCreated mapToAccountCreatedEvent(testcase.domain.model.Account domain) {

        return AccountCreated.newBuilder()
                .setAccountId(domain.getAccountId())
                .setAgency(domain.getAgency())
                .setNumber(domain.getNumber())
                .setCustomerId(domain.getCustomerId())
                .setCreationDate(domain.getCreationDate()
                        .atZone(ZoneId.of(this.dateZone)).toInstant().toEpochMilli())
                .build();
    }

}

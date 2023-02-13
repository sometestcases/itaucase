package testcase.domain.customMocks;

import java.time.ZoneId;

import testcase.domain.mapper.AccountMapper;
import testcase.domain.model.AccountStatus;
import testcase.event.avro.AccountCreated;
import testcase.persistence.entities.Account;

public class TestFakeAccountMapper extends AccountMapper {

    @Override
    public testcase.domain.model.Account map(Account entity) {

        return testcase.domain.model.Account.builder()
                .id(1L)
                .accountId(entity.getAccountId())
                .customerId(entity.getCustomerId())
                .number(entity.getNumber())
                .agency(entity.getAgency().getNumber())
                .creationDate(entity.getCreationDate())
                .status(AccountStatus.valueOf(entity.getStatus().name()))
                .updateDate(entity.getUpdateDate())
                .build();
    }

    @Override
    public AccountCreated mapToAccountCreatedEvent(testcase.domain.model.Account domain) {

        return AccountCreated.newBuilder()
                .setAccountId(domain.getAccountId())
                .setAgency(domain.getAgency())
                .setNumber(domain.getNumber())
                .setCustomerId(domain.getCustomerId())
                .setCreationDate(domain.getCreationDate()
                        .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }
}

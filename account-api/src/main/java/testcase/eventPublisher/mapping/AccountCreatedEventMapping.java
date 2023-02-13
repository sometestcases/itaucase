package testcase.eventPublisher.mapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import testcase.event.avro.AccountCreated;

@Component
public class AccountCreatedEventMapping implements PublishMapping<AccountCreated>{

    @Value("${kafka.topics.account-created}")
    private String accountCreatedTopic;

    @Override
    public Class<AccountCreated> getMappedClass() {
        return AccountCreated.class;
    }

    @Override
    public String getTopic() {
        return this.accountCreatedTopic;
    }

    @Override
    public String getKey(AccountCreated accountCreated) {
        return accountCreated.getAccountId();
    }
}

package testcase.domain.eventPublisher.mapping;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import testcase.event.avro.AccountCreated;
import testcase.event.avro.BalanceUpdate;

@Component
public class BalanceUpdateEventMapping implements PublishMapping<BalanceUpdate>{

    @Value("${kafka.topics.balance-update}")
    private String balanceUpdateTopic;

    @Override
    public Class<BalanceUpdate> getMappedClass() {
        return BalanceUpdate.class;
    }

    @Override
    public String getTopic() {
        return this.balanceUpdateTopic;
    }

    @Override
    public String getKey(BalanceUpdate balanceUpdate) {
        /*
         * Os updates da mesma conta precisam ser consumidos na ordem
         * por isso a chave do evento deve ser o id da conta
         */
        return balanceUpdate.getAccountId();
    }
}

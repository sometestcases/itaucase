package testcase.listener.balanceUpdate;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import testcase.domain.service.BalanceUpdateService;
import testcase.domain.service.exception.AccountBalanceUpdateException;
import testcase.event.avro.BalanceUpdate;
import testcase.listener.config.KafkaSubscriberConfig;

@Slf4j
@Component
public class BalanceUpdateEventListener {

    @Autowired
    private BalanceUpdateService balanceUpdateService;

    @KafkaListener(topics = "${kafka.topics.balance-update}", containerFactory = KafkaSubscriberConfig.BALANCE_UPDATE_CONTAINER_FACTORY, groupId = "${spring.application.name}[${spring.profiles.active}]")
    public void listenBalanceUpdate(final ConsumerRecord<String, BalanceUpdate> event)
            throws AccountBalanceUpdateException {

        BalanceUpdate balanceUpdate = event.value();
        this.balanceUpdateService.updateByEvent(balanceUpdate);
    }
}

package testcase.listener.accountCreation;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import testcase.domain.service.AccountInitializerService;
import testcase.domain.service.exception.AccountAlreadyInitializedException;
import testcase.domain.service.exception.AccountInitializeInternalException;
import testcase.event.avro.AccountCreated;
import testcase.listener.config.KafkaSubscriberConfig;

@Slf4j
@Component
public class AccountCreationEventListener {

    @Autowired
    private AccountInitializerService accountInitializerService;

    @KafkaListener(topics = "${kafka.topics.account-created}", containerFactory = KafkaSubscriberConfig.ACCOUNT_CREATION_CONTAINER_FACTORY, groupId = "${spring.application.name}[${spring.profiles.active}]")
    public void listenCustomerCreation(final ConsumerRecord<String, AccountCreated> event) throws AccountInitializeInternalException {

        try {
            log.info("stage=listen-account-created, accountId={}", event.value().getAccountId());
            this.accountInitializerService.initializeAccount(
                    event.value().getAccountId(),
                    event.value().getNumber(),
                    event.value().getAgency());
        } catch (AccountAlreadyInitializedException e) {
            log.warn("stage=account-already-initialized, accountId={}", event.value().getAccountId());
        } catch (AccountInitializeInternalException e) {
            log.error("stage=error-to-initialize-account, accountId={}", event.value().getAccountId(), e);
            throw e;
        }
    }
}

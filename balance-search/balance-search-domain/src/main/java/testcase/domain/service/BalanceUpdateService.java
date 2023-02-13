package testcase.domain.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import javax.annotation.concurrent.NotThreadSafe;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import testcase.domain.service.exception.AccountBalanceUpdateException;
import testcase.event.avro.BalanceUpdate;
import testcase.persistence.entities.AccountBalance;
import testcase.persistence.repositories.AccountBalanceRepository;
import testcase.persistence.utils.TransactionalComponent;

@Slf4j
@Service
public class BalanceUpdateService {

    @Value("${date.zone}")
    private String dateZone;

    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @Autowired
    private TransactionalComponent transactionalComponent;

    /*
     * Essa operação não é thread safe para o mesmo AccountId
     * toma como base que a chave do tópico do Kafka é o AccountId
     * e por isso nunca vira um evento referente a mesma conta em paralelo
     */
    public void updateByEvent(BalanceUpdate event) throws AccountBalanceUpdateException {

        try {

            log.info("stage=init-balance-update, accountId={}, updateOrder={}",
                    event.getAccountId(), event.getUpdateOrder());

            this.transactionalComponent.require(() -> {
                Optional<AccountBalance> accountBalance =
                        this.accountBalanceRepository.findByAccountId(event.getAccountId());

                if (accountBalance.isEmpty()) {
                    AccountBalance newAccountBalance = new AccountBalance();
                    newAccountBalance.setAccountId(event.getAccountId());
                    newAccountBalance.setUpdateDate(
                            Instant.ofEpochMilli(event.getUpdateDate()).atZone(ZoneId.of(this.dateZone)).toLocalDateTime());
                    newAccountBalance.setBlocked(event.getBlocked());
                    newAccountBalance.setOperationOrder(event.getUpdateOrder());
                    newAccountBalance.setBalance(new BigDecimal(event.getBalance()));
                    this.accountBalanceRepository.save(newAccountBalance);
                    log.info("stage=try-create-new-account-balance-from-event, accountId={}", newAccountBalance.getAccountId());
                } else if (accountBalance.get().getOperationOrder() >= event.getUpdateOrder()) {
                    log.info("stage=old-update-balance-event-discarder, accountId={}, actualOrder={}, receivedOrder={}",
                            accountBalance.get().getAccountId(), accountBalance.get().getOperationOrder(), event.getUpdateOrder());
                } else {
                    accountBalance.get().setOperationOrder(event.getUpdateOrder());
                    accountBalance.get().setBlocked(event.getBlocked());
                    accountBalance.get().setUpdateDate(
                            Instant.ofEpochMilli(event.getUpdateDate()).atZone(ZoneId.of(this.dateZone)).toLocalDateTime());
                    accountBalance.get().setBalance(new BigDecimal(event.getBalance()));
                    log.info("state=account-balance-updated, accountId={}, newOrder={}",
                            accountBalance.get().getId(), accountBalance.get().getOperationOrder());
                }
            });
        } catch (Exception ex) {
            log.error("stage=error-to-update-account-balance, accountId={}", event.getAccountId(), ex);
            throw new AccountBalanceUpdateException(ex);
        }
    }
}

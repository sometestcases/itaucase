package testcase.api.resources;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import testcase.domain.service.BalanceBlockService;
import testcase.domain.service.BalanceSearchService;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.model.AccountBalance;

@RequestMapping("/balances")
@RestController
public class BalanceResource {

    @Autowired
    private BalanceBlockService balanceBlockService;

    @Autowired
    private BalanceSearchService balanceSearchService;

    @PatchMapping(path = "/{accountId}/block", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> executeOperation(
            @PathVariable("accountId") String accountId) throws BalanceAtomicOperationException {

        this.balanceBlockService.blockAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountBalance> searchByAccountId(
            @PathVariable("accountId") String accountId) {

        return this.balanceSearchService
                .findByAccountId(accountId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

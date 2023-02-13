package testcase.api.resources;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import testcase.api.mapper.DomainToResponseMapper;
import testcase.api.responses.AccountBalanceResponse;
import testcase.domain.model.AccountBalanceState;
import testcase.domain.service.BalanceSearchService;
import testcase.domain.service.exception.AccountBalanceBlockedException;
import testcase.domain.service.exception.AccountBalanceNotFoundException;
import testcase.domain.service.exception.AccountBalanceSearchException;

@RequestMapping("/balances")
@RestController
public class BalanceSearchResource {

    @Autowired
    private BalanceSearchService balanceSearchService;

    @GetMapping(path = "/{accountId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountBalanceResponse> executeInternalTransfer(
            @PathVariable("accountId") String accountId,
            @RequestParam(value = "requireUnblocked", required = false) Boolean requireUnblocked)
            throws AccountBalanceNotFoundException, AccountBalanceBlockedException, AccountBalanceSearchException {

        AccountBalanceState accountBalanceState =
                this.balanceSearchService.findByAccountId(accountId,
                        Optional.ofNullable(requireUnblocked).orElse(true));

        return ResponseEntity.ok(DomainToResponseMapper.map(accountBalanceState));
    }
}

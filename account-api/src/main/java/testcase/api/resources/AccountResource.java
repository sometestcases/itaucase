package testcase.api.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import testcase.api.requests.CreateAccountRequest;
import testcase.domain.model.Account;
import testcase.domain.service.AccountService;
import testcase.domain.service.exception.AlreadyExistAccountException;
import testcase.domain.service.exception.AlreadyInactiveAccountException;
import testcase.domain.service.exception.CantBlockBalanceException;
import testcase.domain.service.exception.InactiveCustomerException;
import testcase.domain.service.exception.InexistentAccountException;
import testcase.domain.service.exception.InexistentCustomerException;
import testcase.domain.service.exception.InternalAccountCreationException;
import testcase.domain.service.exception.InternalAccountInactivateException;

@RequestMapping(value = "/accounts")
@RestController
public class AccountResource {
    @Autowired
    private AccountService accountService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(
            @Validated @RequestBody CreateAccountRequest createAccountRequest) throws
            InexistentCustomerException,
            InactiveCustomerException,
            AlreadyExistAccountException,
            InternalAccountCreationException {

        Account account = this.accountService.create(
                createAccountRequest.getCustomerId(),
                createAccountRequest.getAgency());

        final URI resourceURI = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{accountId}")
                .buildAndExpand(account.getAccountId()).toUri();

        return ResponseEntity.created(resourceURI).build();
    }

    @DeleteMapping(path = "/customer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(
            @PathVariable("customerId") String customerId) throws
            AlreadyInactiveAccountException,
            CantBlockBalanceException,
            InexistentAccountException,
            InternalAccountInactivateException {

        this.accountService.inactivateAccountByCustomerId(customerId);
        return ResponseEntity.ok().build();
    }
}

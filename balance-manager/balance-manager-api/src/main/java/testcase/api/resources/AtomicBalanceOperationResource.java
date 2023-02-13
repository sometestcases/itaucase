package testcase.api.resources;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import testcase.api.requests.BalanceOperationRequest;
import testcase.api.requests.mapper.RequestToDomainMapper;
import testcase.domain.service.BalanceOperationService;
import testcase.domain.service.exception.AlreadyExecutedOperationException;
import testcase.domain.service.exception.BalanceAtomicOperationException;
import testcase.domain.service.exception.BalanceOperationSizeExcededException;

@RestController
public class AtomicBalanceOperationResource {

    @Autowired
    private BalanceOperationService balanceOperationService;

    @PostMapping(path = "/atomicBalanceOperation",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> executeOperation(
            @Validated @RequestBody BalanceOperationRequest balanceOperationRequest)
            throws BalanceAtomicOperationException,
            AlreadyExecutedOperationException,
            BalanceOperationSizeExcededException {

        this.balanceOperationService.executeOperation(RequestToDomainMapper.map(balanceOperationRequest));

        return ResponseEntity.ok().build();
    }
}
